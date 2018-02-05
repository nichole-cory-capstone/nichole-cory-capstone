 $(document).ready(function () {
        var map;
        var infowindow;
        var service;
        var curLocation = {lat: parseFloat(latStr),
            lng: parseFloat(lonStr)
        };
        var autocompleteOptions = {
            componentRestrictions: {
                "country": "us"
            }
        };
        var getNextPage = null;
        var moreButton = document.getElementById('more-btn');
        moreButton.onclick = function() {
            $('#more-btn').addClass('loading');
            // moreButton.disabled = true;
            if (getNextPage) getNextPage();
        };
        var markers = [];
        var acInput = document.getElementById("autocompleteMap");
        var autocomplete = new google.maps.places.Autocomplete(acInput, autocompleteOptions);

        $('#searchForm').on("submit", function(e){
            e.preventDefault();
            console.log("click");
            var val = $('#search-box').val();
            $('#search-btn').addClass('loading');
            search(curLocation,val);
        });


        //https://stackoverflow.com/questions/7095574/google-maps-api-3-custom-marker-color-for-default-dot-marker/7686977#7686977
        //Listener for the add POI button in the infowindows
        function addListener(placeId) {
            $('#' + placeId).click(function() {
                $.ajax({
                    url: "/user/trips/poi/",
                    type: "POST",
                    data: {
                        placeId: this.id
                    },
                    headers: {'X-CSRF-TOKEN': csrfToken},
                    beforeSend: function () {
                        $('#' + placeId).addClass('loading');
                    }
                }).done(function (data) {
                    $('#' + placeId).removeClass('loading').addClass('positive').text('Added!');
                    // var result = JSON.parse(data);
                    pointsOfInterest = data;
                }).fail(function (jqXhr, status, error) {
                    $('#' + placeId).removeClass('loading').addClass('negative').text('Error!');
                    console.log("Error");
                });
            })
        }




     initMap();
        // getLocation();

        // Bias the autocomplete object to the user's geographical location,
        // as supplied by the browser's 'navigator.geolocation' object.
        function geolocate() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    var geolocation = {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude
                    };
                    var circle = new google.maps.Circle({
                        center: geolocation,
                        radius: position.coords.accuracy
                    });
                    autocomplete.setBounds(circle.getBounds());
                });
            }
        }

        // Geo Location
        function getLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(initMap);
                // $autocomplete.onfocus(geolocate());
            } else {
                x.innerHTML = "Geolocation is not supported by this browser.";
            }
        }

        function initMap() {

            // $("#wrapper").fadeIn();

            // var curLocation = {lat: position.coords.latitude, lng: position.coords.longitude};

            map = new google.maps.Map(document.getElementById('map'), {
                center: curLocation,
                zoom: 13
            });

            infowindow = new google.maps.InfoWindow();

            service = new google.maps.places.PlacesService(map);

            autocomplete.addListener('place_changed', onPlaceChanged);

            // search(curLocation);
            poiLoader(pointsOfInterest);
        }

        function onPlaceChanged() {
            $("#wrapper").fadeIn();
            var place = autocomplete.getPlace();
            if (place.geometry) {
                map.panTo(place.geometry.location);
                map.setZoom(15);
                clearMarkers();
                search(place.geometry.location);
            } else {
                // acInput.setPlaceholder("Type the city, address or zip code");
            }
        }

        function search(curLocation,term) {
            service.nearbySearch({
                location: curLocation,
                radius: 1000,
                type: ['food'],
                keyword: term
            }, callback);

        }

        function clearMarkers() {
            for (var i = 0; i < markers.length; i++) {
                if (markers[i]) {
                    markers[i].setMap(null);
                }
            }
            markers = [];
        }

        function callback(results, status, pagination) {

            $('#search-btn').removeClass('loading');
            $('#more-btn').removeClass('loading');
            if (status !== 'OK') return;

            clearMarkers();

            for (var i = 0; i < results.length; i++) {
                addMarker(results[i]);
            }

            moreButton.disabled = !pagination.hasNextPage;
            getNextPage = pagination.hasNextPage && function() {
                pagination.nextPage();
            };
        }


        function addMarker(place) {
             console.log(place);
            var marker = setupMarker(searchByValue(place, pointsOfInterest), place);

            markers.push(marker);
        }

        function setupMarker(inList, place) {
            var card;
            var pinColor;
            var photos = place.photos;

            if (!photos) {
                return;
            }
            var photo = photos[0].getUrl({'maxWidth': 150, 'maxHeight': 150});
            // var content = "<h6>" + place.name + "</h6>" +
            //     "<br/>" +
            //     "<img class='center-align center' src='" + photo + "' /><br/>";

            if(inList){
                 card = '<div class="ui cards">'+
                    '<div class="card">' +
                    '<div class="content">' +
                    '<div class="header">' + place.name + '</div>' +
                    '<div class="description">' +
                    '<img src="' + photo + '"/>' +
                    '</div>' +
                    '</div>' +
                    '</div>';
                pinColor = "0f05fe";
            }else {
                 card = '<div class="ui cards">'+
                    '<div class="card">' +
                    '<div class="content">' +
                    '<div class="header">' + place.name + '</div>' +
                    '<div class="description">' +
                    '<img src="' + photo + '"/>' +
                    '</div>' +
                    '</div>' +
                    '<button id="'+ place.id + '" class="poi ui bottom attached button fluid">' +
                    '<i class="add icon"></i>' +
                    'Add to your collection' +
                    '</button>' +
                    '</div>';
                pinColor = "df691a";

            }
            var pinImage = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|" + pinColor,
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
            google.maps.event.addListener(marker, 'click', function() {
                infowindow.setContent(card);
                infowindow.open(map, this);
            });

            //Found at http://en.marnoto.com/2014/09/5-formas-de-personalizar-infowindow.html
            google.maps.event.addListener(infowindow, 'domready', function(){
                addListener(place.id);
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
            });
         return marker;
        }

        function searchByValue (place, pointsOfInterest){
            var bool = false;
            pointsOfInterest.forEach(function (value) {
                if (place.id === value.placeId) {
                    bool = true;
                }
            });
            return bool;
           }

        function poiLoader (pointsOfInterest){

            function callback(place, status) {
                console.log("Callback");
                if (status === google.maps.places.PlacesServiceStatus.OK) {
                    console.log(place);
                    addMarker(place);
                }
            }

            for (var i = 0; i < pointsOfInterest.length; i++) {
                var request = {placeId: "'" + pointsOfInterest[i].placeId + "'"};
                console.log(request);
                service.getDetails(request, callback);
            }
        }

        function setMapOnAll(map) {
            for (var i = 0; i < markers.length; i++) {
                markers[i].setMap(map);
            }
        }
 });
