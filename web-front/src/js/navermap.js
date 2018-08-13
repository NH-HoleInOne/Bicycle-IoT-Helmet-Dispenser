var map = new naver.maps.Map("Map", {
    center: new naver.maps.LatLng(37.6354588, 127.2115555),
    zoom: 11, //기본줌
    minZoom: 2, //최소줌 
    zoomControl: true,//줌 조절 바 
    zoomControlOptions: // 줌 옵션 
    { //줌 컨트롤의 옵션
        position: naver.maps.Position.TOP_RIGHT
    }
});
var infoWindow = new naver.maps.InfoWindow({
    anchorSkew: true
});
map.setCursor('pointer');
// search by tm128 coordinate
function searchCoordinateToAddress(latlng) {
    var tm128 = naver.maps.TransCoord.fromLatLngToTM128(latlng);
    infoWindow.close();
    naver.maps.Service.reverseGeocode({
        location: tm128,
        coordType: naver.maps.Service.CoordType.TM128
    }, function (status, response) {
        if (status === naver.maps.Service.Status.ERROR) {
            return alert('Something Wrong!');
        }
        var items = response.result.items,
            htmlAddresses = [];


    });
}
// result by latlng coordinate
function searchAddressToCoordinate(address) {
    naver.maps.Service.geocode({
        address: address
    }, function (status, response) {
        if (status === naver.maps.Service.Status.ERROR) {
            return alert('Something Wrong!');
        }
        var item = response.result.items[0],
            point = new naver.maps.Point(item.point.x, item.point.y);

        map.setCenter(point);
    });
}
function initGeocoder() {
    map.addListener('click', function (e) {
        searchCoordinateToAddress(e.coord);
    });
    $('#address').on('keydown', function (e) {
        var keyCode = e.which;
        if (keyCode === 13) { // Enter Key
            searchAddressToCoordinate($('#address').val());
        }
    });
    $('#submit').on('click', function (e) {
        e.preventDefault();
        searchAddressToCoordinate($('#address').val());
    });
    //searchAddressToCoordinate('남양주시 금곡로');
}
naver.maps.onJSContentLoaded = initGeocoder;



let markerLocations = [
    // new naver.maps.LatLng(37.6342988, 127.2127505),
    // new naver.maps.LatLng(37.6347071, 127.2101761),
    // new naver.maps.LatLng(37.636196, 127.2108617),
    // new naver.maps.LatLng(37.6366184, 127.2140564),
    // new naver.maps.LatLng(37.6353122, 127.2069777),
    // new naver.maps.LatLng(37.6346033, 127.2082725),
    // new naver.maps.LatLng(37.6352985, 127.2161412),
]

let markers = [], infoWindows = [], markerNames = [], markerIcons = []

let eraseAllMarkers = function () {
    for (marker of markers) {
        marker.setMap(null)
    }
}
let addMarkers = function (locations) {
    markerLocations = locations;
    markers = []
    for(let i=0; i<markerLocations.length; i++){
        markers.push(
            new naver.maps.Marker({
                map: map,
                position: markerLocations[i],
                icon: {
                    url: markerIcons[i],
                    size: new naver.maps.Size(70, 70),
                    origin: new naver.maps.Point(0, 0),
                    anchor: new naver.maps.Point(25, 26),
                }
            })
        )
        infoWindows.push(
            new naver.maps.InfoWindow({
                content: `
                    <div>
                        <h4>${markerNames[i]}</h4>
                        자전거 대여소
                    </div>
                `
            })
        )
    }

    for (let i = 0; i < markers.length; i++) {
        let marker = markers[i]
        let infoWindow = infoWindows[i]
        naver.maps.Event.addListener(marker, "click", function (e) {
            if (infoWindow.getMap()) {
                infoWindow.close();
            } else {
                infoWindow.open(map, marker);
                decideLocation(markerLocations[i]);
            }
        })
    }
}
eraseAllMarkers();
addMarkers(markerLocations);


function decideLocation(location) {

    let restRequest = new XMLHttpRequest;
    if (!restRequest) {
        console.error("restRequest=0; error");
    }
    restRequest.onreadystatechange = restResponse;
    let x = location.lat()
    let y = location.lng()
    restRequest.open('GET', `http://10.0.100.94:8080/findByLoc?x=${x}&y=${y}`);
    restRequest.setRequestHeader("Content-Type", "application/json");
    restRequest.send();

    function restResponse() {
        if (restRequest.readyState === XMLHttpRequest.DONE) {
            if (restRequest.status === 200) {
                console.log('restRequest : ' + restRequest.response)
                let response = JSON.parse(restRequest.response)[0]

                let location = response.region
                let goodHelemt = response.helmetNum
                let alcohol = response.antiLeft
                let badHelmets = response.breakCode
                let badHelmet = response.breakNum
                if(badHelmets){
                    badHelmets = badHelmets.map(hid =>
                        ({hid: '0'.repeat(4 - String(hid).length) + hid})
                    )
                } else {
                    badHelmets = []
                }
                
                vm.location = location
                vm.badHelmets = badHelmets
                vm.helmetStorage = {
                    good: goodHelemt,
                    alcohol: alcohol,
                    bad: badHelmet,
                }
            } else {
                console.error('request에 뭔가 문제가 있어요.');
                vm.initialize();
            }
        }
    }
}

function findMarker() {
    let restRequest = new XMLHttpRequest;
    if (!restRequest) {
        console.error("restRequest=0; error");
    }
    restRequest.onreadystatechange = restResponse;
    restRequest.open('GET', 'http://10.0.100.94:8080');
    restRequest.send();

    function restResponse() {
        if (restRequest.readyState === XMLHttpRequest.DONE) {
            if (restRequest.status === 200) {
                console.log('restRequest : ' + restRequest.response)
                let response = JSON.parse(restRequest.response)
                console.log('parsing : ' + response)

                markerLoations = []
                markerNames = []
                markerIcons = []

                for(let i=0; i<response.length; i++){
                    data = response[i]

                    markerLocations.push(
                        new naver.maps.LatLng(data.x, data.y)
                    )
                    markerNames.push(
                        data.region
                    )
                    if(data.helmetNum > data.bikeNum){
                        markerIcons.push('/../resource/red-mark.png')
                    } else if (data.helmetNum == data.bikeNum){
                        markerIcons.push('/../resource/yellow-mark.png')
                    } else {
                        markerIcons.push('/../resource/blue-mark.png')
                    }
                }
                eraseAllMarkers();
                addMarkers(markerLocations);

            } else {
                console.error(`${restRequest.status} : ${restRequest.statusText} `)
            }
        }
    }
}


// let infinite_loop_finding = (setInterval(function(){
//     findMarker()
// }, 1000))()

let once_finding = findMarker();
