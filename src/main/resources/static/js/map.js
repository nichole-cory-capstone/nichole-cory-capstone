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
                    console.log(data);
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

            search(curLocation);
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
            var photos = place.photos;

            if (!photos) {
                return;
            }

            var photo = photos[0].getUrl({'maxWidth': 150, 'maxHeight': 150});
            var content = "<h6>" + place.name + "</h6>" +
                "<br/>" +
                "<img class='center-align center' src='" + photo + "' /><br/>";

            var cardOld = '<div class="ui cards">'+
                '<div class="card">' +
                '<div class="content">' +
                '<div class="header">' + place.name + '</div>' +
                '<div class="description">' +
                '<img src="' + photo + '"/>' +
                '</div>' +
                '</div>' +
                '<form action="/user/trips/poi/" method="POST" >' +
                '<button  class="poi ui bottom attached button fluid">' +
                '<input type="hidden" name="' + csrfParam
                + '" value="' + csrfToken + '"/>' +
                '<input type="hidden" name="placeId" value="'+ place.id + '">' +
                '<i class="add icon"></i>' +
                'Add to your collection' +
                '</button></form>' +
                '</div>';

            var card = '<div class="ui cards">'+
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


            var marker = new google.maps.Marker({
                map: map,
                position: place.geometry.location,
                title: place.name
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
            markers.push(marker);
        }

        function setMapOnAll(map) {
            for (var i = 0; i < markers.length; i++) {
                markers[i].setMap(map);
            }
        }
 });
