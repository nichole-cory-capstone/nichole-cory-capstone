$(document).ready(function () {
    var map;
    var infowindow;
    var service;
    var curTerm = null;
    var curLocation = {lat: parseFloat(latStr),
        lng: parseFloat(lonStr)
    };
    var notification = window.Notification || window.mozNotification || window.webkitNotification;
    var getNextPage = null;
    var moreButton = document.getElementById('more-btn');
    moreButton.onclick = function() {
        $('#more-btn').addClass('loading');
        // moreButton.disabled = true;
        if (getNextPage) getNextPage();
    };
    var markers = [];
    var listenerHandler = [];
    var fences = [];
    var fenceNotify = [];
    var locationMarker = null;
    var positionTimer = null;
    var circleRadius = (.05 * 1000);
    var locationOn = false;
    google.maps.Circle.prototype.contains = function(latLng) {
        return this.getBounds().contains(latLng) && google.maps.geometry.spherical.computeDistanceBetween(this.getCenter(), latLng) <= this.getRadius();
    };

    if ('undefined' === typeof notification)
        console.log('Web notification not supported');
    else
        notification.requestPermission(function(permission){});


    $('#searchForm').on("submit", function(e){
        e.preventDefault();
        curTerm = $('#search-box').val();
        $('#search-btn').addClass('loading');
        search(curLocation,curTerm);
    });


    //https://stackoverflow.com/questions/7095574/google-maps-api-3-custom-marker-color-for-default-dot-marker/7686977#7686977
    //Listener for the add POI button in the infowindows
    function infoWindowListener(placeId) {
        var url;
        var buttonText = $('#' + placeId).text();

        if (buttonText === "Save") {
            url = "/user/trips/poi/";
        } else if (buttonText === "Remove") {
            url = "/user/trips/poi/remove";
        }

        $('#' + placeId).click(function () {
            if(authenticated) {
                $.ajax({
                    url: url,
                    type: "POST",
                    data: {
                        placeId: this.id
                    },
                    headers: {'X-CSRF-TOKEN': csrfToken},
                    beforeSend: function () {
                        $('#' + placeId).addClass('loading');
                    }
                }).done(function (data) {
                    $('#' + placeId).removeClass('loading').addClass('positive').text('Success');
                    // var result = JSON.parse(data);
                    pointsOfInterest = data;
                    clearMarkers();
                    poiLoader(pointsOfInterest);
                    if (curTerm !== null) {
                        search(curLocation, curTerm);
                    }
                }).fail(function (jqXhr, status, error) {
                    $('#' + placeId).removeClass('loading').addClass('negative').text('Error');
                    console.log("Error");
                });
            }else{
                $('.loginmodal').modal('toggle');
            }
        })
    }

//
//     function initialize() {
//
//         var map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
//
// // Resize the map on mobile
//         google.maps.event.addDomListener(window, "resize", function() {
//             var center = map.getCenter();
//             google.maps.event.trigger(map, "resize");
//             map.setCenter(center);
//         });

    initMap();

    function initMap() {

        map = new google.maps.Map(document.getElementById('map'), {
            center: curLocation,
            zoom: 14
        });

        infowindow = new google.maps.InfoWindow();

        service = new google.maps.places.PlacesService(map);

        // autocomplete.addListener('place_changed', onPlaceChanged);

        // search(curLocation);
        poiLoader(pointsOfInterest);
    }

    // function onPlaceChanged() {
    //     $("#wrapper").fadeIn();
    //     var place = autocomplete.getPlace();
    //     if (place.geometry) {
    //         map.panTo(place.geometry.location);
    //         map.setZoom(15);
    //         clearMarkers();
    //         search(place.geometry.location);
    //     } else {
    //         // acInput.setPlaceholder("Type the city, address or zip code");
    //     }
    // }

    function search(curLocation,term) {
        service.nearbySearch({
            location: curLocation,
            radius: 4000,
            keyword: term
        }, callback);

    }

    function clearMarkers() {
        for (var i = 0; i < markers.length; i++) {
            if (markers[i]) {
                markers[i].setMap(null);

            }
        }
        for(i = 0; i < listenerHandler.length; i++){
            google.maps.event.removeListener(listenerHandler[i]);
        }
        removeFences();
        markers = [];
    }

    function callback(results, status, pagination) {

        $('#search-btn').removeClass('loading');
        $('#more-btn').removeClass('loading');
        if (status !== 'OK') {
            console.log("Search Error");
            console.log(curLocation);
            console.log(curTerm);
            return;
        }

        clearMarkers();

        for (var i = 0; i < results.length; i++) {
            service.getDetails({placeId: results[i].place_id}, function(place, status) {
                if (status === google.maps.places.PlacesServiceStatus.OK) {
                    addMarker(place);
                }else{
                    addMarker(results[i]);
                }
            })
        }

        poiLoader(pointsOfInterest);
        moreButton.disabled = !pagination.hasNextPage;
        getNextPage = pagination.hasNextPage && function() {
            pagination.nextPage();
        };
    }


    function addMarker(place) {
        var yelpData;
        $.ajax({
            url: "/search/yelp/",
            type: "POST",
            data: {
                name: place.name,
                lat: place.geometry.location.lat(),
                lon: place.geometry.location.lng()
            },
            headers: {'X-CSRF-TOKEN': csrfToken}
        }).done(function (data) {
           yelpData = JSON.parse(data);
        }).fail(function (jqXhr, status, error) {
            yelpData = error;
            return "error";
        }).always(function () {
            openTableSearch(place,yelpData);
        });
    }

    function setupMarker(inList, place, yelpInfo,otData) {
        console.log(yelpInfo);
        var card;
        var pinColor;

        var photos = "";
         try{
            photos = place.photos;
        }catch (e){
            if(e){
                //Boo google
            }
        }
        var photo = photos[0].getUrl({'maxWidth': 150, 'maxHeight': 150});

        var vicinity = "";
        try{
            if( typeof place.vicinity !== "undefined"){
                vicinity = place.vicinity;
            }
        }catch (e){
            if(e){
                //Boo google
            }
        }

        var hours = "";
        try{
            if(place.opening_hours.weekday_text.length === 0 || typeof place.opening_hours.weekday_text === "undefined"){
               hours = "";
            }else {
                hours = "Hours: " + place.opening_hours.weekday_text;
            }
        }catch (e){
            if(e){
                //Boo google
            }
        }

        var rating = "";
        try{
            if(typeof place.rating !== "undefined"){
                rating = "Rating: " + place.rating;
            }
        }catch (e){
            if(e){
                //Boo google
            }
        }

        var phone = "";
        try{
             if(typeof place.formatted_phone_number !== "undefined"){
                phone = "Phone:  " + '<a href="tel:' + place.formatted_phone_number + '">'+ place.formatted_phone_number + '</a>';
             }
        }catch (e){
            if(e){
                //Boo google
            }
        }

        var website = "";
        var websiteText = "";
        var tagText = "";
        try{
             if(typeof place.website !== "undefined"){
                website = place.website;
                websiteText = "Website: ";
                tagText = " " + place.website;
             }
        }catch (e){
            if(e){
                //Boo google
            }
        }

        var mapsUrl = "";
        try{
            if(typeof place.url !== "undefined"){
                mapsUrl =  '<a style="color: #fff;" href="'+ place.url +'" target="_blank"><i class="far fa-map fa-3x modal-icon"></i></a>'
             }
        }catch (e){
            if(e){
                //Boo google
            }
        }

        var openTableUrl = "";
        try{
            if(typeof otData.restaurants[0].mobile_reserve_url !== "undefined"){
                openTableUrl =  '<a style="color: #fff;  size=9px;" href="'+ otData.restaurants[0].mobile_reserve_url +'" target="_blank"><img src="/images/ot_logo.png" class="modal-icon" style="width: 43px; height: 34px;"/></a>'
            }
        }catch (e){
            if(e){
                //Boo google
            }
        }


        console.log(place);

        var uberLink = "https://m.uber.com/ul/?action=setPickup&client_id=t2-NM5_Rw6g3kvW918lcV9I3F6HHlCqx&pickup=my_location&dropoff[formatted_address]=";
        var infomodal = '<div class="ui align center demo modal button tiny tripinfomodal" id="moreinfo">' +
            '<div class="ui aligned center">' + '<h2 class="ui image header"><div class="content">' + place.name +  '</div></h2>' +
            '<p>' + vicinity + '</p>' +
            '<p>' +  hours + '</p>' +
            '<p>' +  rating + '</p>' +
            '<p>' +  phone + '</p>' +
            '<p>' +  websiteText + '<a href="'+ website +'" target="_blank"> ' + tagText + ' </a>' + '</p>' +
            '<a style="color: #fff;" href="'+ uberLink + place.formatted_address + '&dropoff[latitude]='+place.geometry.location.lat() + '&dropoff[longitude]='+place.geometry.location.lng() + '" target="_blank"><i class="fab fa-uber fa-3x modal-icon"></i></a>' +
            mapsUrl +
            openTableUrl +
            '<div style="display: inline" class="ui accordion">' +
            '<div style="display: inline" class="title">' +
            '<i class="fab fa-yelp fa-3x icon modal-icon"></i>' +
            '</div>' +
            '<div class="content">' +
            '<p class="transition hidden">yelp content</p>' +
            '</div>' +
            '</div>';



        if(inList){
            card = '<div class="ui cards">'+
                '<div class="card">' +
                '<div class="content">' +
                '<div class="header">' + place.name + '</div>' +
                '<div class="description">' +
                '<img src="' + photo + '"/>' +
                '</div>' +
                '</div>' +
                '<div class="ui buttons">'+
                '<button id="'+ place.place_id + '" class="ui warning button ">' +
                'Remove' +
                '</button>' +
                '<button id="'+ place.place_id + '-more" class="ui info button">' +
                'More Info' +
                '</button>' +
                '</div>' +
                infomodal;
            pinColor = "2B3E50";
        }else {
            card = '<div class="ui cards">'+
                '<div class="card">' +
                '<div class="content">' +
                '<div class="header">' + place.name + '</div>' +
                '<div class="description">' +
                '<img src="' + photo + '"/>' +
                '</div>' +
                '</div>' +
                '<div class="ui buttons">'+
                '<button id="'+ place.place_id + '" class="ui button">' +
                '<i class="add icon"></i>' +
                'Save' +
                '</button>' +
                '<button id="'+ place.place_id + '-more" class="ui info button"> ' +
                'More Info' +
                '</button>' +
                '</div>' +
                infomodal;
            pinColor = "DF691A";

        }


        var pinImage = new google.maps.MarkerImage("https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|" + pinColor,
            new google.maps.Size(21, 34),
            new google.maps.Point(0,0),
            new google.maps.Point(10, 34));


        var marker = new google.maps.Marker({
            map: map,
            position: place.geometry.location,
            title: place.name,
            icon: pinImage
            // icon: photos[0].getUrl({'maxWidth': 50, 'maxHeight': 50})
        });

        //Add geofence if the marker is a saved poi
        if(inList) {
            addFence(marker);
        }

        listenerHandler.push(google.maps.event.addListener(marker, 'click', function() {
            infowindow.setContent(card);
            infowindow.open(map, this);
        }));

        //Found at http://en.marnoto.com/2014/09/5-formas-de-personalizar-infowindow.html
        listenerHandler.push(google.maps.event.addListener(infowindow, 'domready', function(){
            infoWindowListener(place.place_id);
            moreInfo(place.place_id);

            // Reference to the DIV which receives the contents of the infowindow using jQuery
            var iwOuter = $('.gm-style-iw');
            var iwBackground = iwOuter.prev();

            // Remove the background shadow DIV
            iwBackground.children(':nth-child(2)').css({'display' : 'none'});

            // Remove the white background DIV
            iwBackground.children(':nth-child(4)').css({'display' : 'none'});
            var iwCloseBtn = iwOuter.next();

            //Apply the desired effect to the close button
            iwCloseBtn.css({
                // opacity: '1', // by default the close button has an opacity of 0.7
                right: '38px', top: '10px' // button repositioning
                // border: '7px solid #DF691A', // increasing button border and new color
                // 'border-radius': '15px', // circular effect
                // 'box-shadow': '0 0 5px #3990B9' // 3D effect to highlight the button
            });

            // The API automatically applies 0.7 opacity to the button after the mouseout event.
            // This function reverses this event to the desired value.
            // iwCloseBtn.mouseout(function(){
            //     $(this).css({opacity: '1'});
            // });
        }));
        return marker;
    }

    function moreInfo(placeId) {
        $('#' + placeId + '-more').click(function () {

            $('.tripinfomodal').modal({onHide: function(){
                    $('.tripinfomodal').remove();
                }}).modal('toggle')
        });
        $('.ui.accordion')
            .accordion()
        ;
    }



    function searchByValue (place, pointsOfInterest){
        var bool = false;
        for (var i= 0; i < pointsOfInterest.length; i++) {
            if (place.place_id === pointsOfInterest[i].placeId) {
                bool = true;
            }
        }
        return bool;
    }

     function poiLoader (pointsOfInterest){
        for (var i = 0; i < pointsOfInterest.length; i++) {
            service.getDetails({placeId: pointsOfInterest[i].placeId}, function(place, status){
                if (status === google.maps.places.PlacesServiceStatus.OK) {
                    addMarker(place);
                }

            })
        }
    }

    google.maps.event.addListener(map, 'dragend', function () {
        var center = map.getCenter();
        curLocation = {lat: center.lat(), lng: center.lng()};
        if(curTerm !== null) {
            search(curLocation, curTerm);
        }
        if(locationOn) {
            enableLocation();
        }
    });



    function openTableSearch(place,yelpData){
        var otData;
        var city = place.vicinity.split(",").pop(-1).trim();
        $.ajax({
            url:  "https://opentable.herokuapp.com/api/restaurants",
            type: "GET",
            data: {
                name: place.name,
                city: city,
                per_page: 5
            }
        }).done(function (data) {
            otData = data;
        }).fail(function (jqXhr, status, error) {
            console.log("Error");
        }).always(function () {
            var marker = setupMarker(searchByValue(place, pointsOfInterest), place,yelpData,otData);
            markers.push(marker);
        });

    }



    //Geo Fences


   function addFence(marker) {
           var circleOptions = {
               strokeColor: 'grey',
               strokeOpacity: 0.4,
               strokeWeight: .5,
               fillColor: 'grey',
               fillOpacity: 0.3,
               // visible: false,
               map: map, //pass the map object to show on the map.
               center: marker.getPosition(), //or you can pass a google.maps.LatLng object
               radius: parseInt(circleRadius) //radius of the circle in metres
           };
           fences.push(new google.maps.Circle(circleOptions));
           fenceNotify.push(false);
       }

    function removeFences(){
       fences.forEach(function (fence){
           fence.setMap(null);
       });
        fences = [];
    }


    //Geo location


   function userMarker(latitude, longitude, label) {
       var im = '/images/bluecircle.png';

       var marker = new google.maps.Marker({
               map: map,
               position: new google.maps.LatLng(
                   latitude,
                   longitude
               ),
               title: (label || ""),
               icon: im
           });

           return( marker );
       }

    function updateUserMarker( marker, latitude, longitude, label ){
     // Update the position.
        marker.setPosition(
            new google.maps.LatLng(
                latitude,
                longitude
            )
        );
      //Check if the user is within the fence
        for (var i = 0; i < fences.length; i++ ){
            if(fenceNotify[i] === false){
                Notify("Your location is nearby!");
                fenceNotify[i] = true;
                console.log("Fence Notify");
            }
        }
        // fences.forEach(function (fence){
        //       if(fence.contains(marker.getPosition())){
        //           Notify("Inside Fence!");
        //           console.log("Inside Fence")
        // }

      // Update the title if it was provided.
        if (label){
            marker.setTitle( label );
        }
     }

   function enableLocation(){
    if (navigator.geolocation) {
        locationOn = true;
        // This is the location marker that we will be using
        // on the map. Let's store a reference to it here so
        // that it can be updated in several places.


        // Get the location of the user's browser using the
        // native geolocation service. When we invoke this method
        // only the first callback is required. The second
        // callback - the error handler - and the third
        // argument - our configuration options - are optional.
        navigator.geolocation.getCurrentPosition(
            function (position) {

                // Check to see if there is already a location.
                // There is a bug in FireFox where this gets
                // invoked more than once with a cached result.
                if (locationMarker) {
                    return;
                }

                // Log that this is the initial position.
                console.log("Initial Position Found");

                // Add a marker to the map using the position.
                locationMarker = userMarker(
                    position.coords.latitude,
                    position.coords.longitude,
                    "Initial Position"
                );

            },
            function (error) {
                console.log("Something went wrong: ", error);
            },
            {
                timeout: (5 * 1000),
                maximumAge: (1000 * 60 * 15),
                enableHighAccuracy: true
            }
        );
    }


        positionTimer = navigator.geolocation.watchPosition(
           function( position ){

               // Log that a newer, perhaps more accurate
               // position has been found.
               console.log( "Newer Position Found" );

               // Set the new position of the existing marker.
               updateUserMarker(
                   locationMarker,
                   position.coords.latitude,
                   position.coords.longitude,
                   "Updated / Accurate Position"
               );

           }
       );


               // If the position hasn't updated within 5 minutes, stop
               // monitoring the position for changes.
       setTimeout(
           function(){
               // Clear the position watcher.
               navigator.geolocation.clearWatch( positionTimer );
           },
           (1000 * 60 * 5)
       );

   }


   function disableLocation(){
        locationMarker.setMap(null);
        locationMarker = null;
        navigator.geolocation.clearWatch( positionTimer );
        locationOn = false;
   }


    function Notify(titleText, bodyText)
    {
        if ('undefined' === typeof notification)
            return false;       //Not supported....
        var noty = new notification(
            titleText, {
                body: bodyText,
                dir: 'auto', // or ltr, rtl
                lang: 'EN', //lang used within the notification.
                tag: 'notificationPopup', //An element ID to get/set the content
                icon: '' //The URL of an image to be used as an icon
            }
        );
        noty.onclick = function () {
            console.log('notification.Click');
        };
        noty.onerror = function () {
            console.log('notification.Error');
        };
        noty.onshow = function () {
            console.log('notification.Show');
        };
        noty.onclose = function () {
            console.log('notification.Close');
        };
        return true;
    }

    $('.ui.checkbox').checkbox();

    $('#location-toggle').change(function() {
        if (this.checked) {
            enableLocation();
            $('#loc-toggle-label').text("Disable Location")
        }else if(!this.checked){
            disableLocation();
            $('#loc-toggle-label').text("Enable Location")

        }
    });


});
