
//지도 위치 
var map = new naver.maps.Map('Map',
    //37.6345054 127.2087829
    {
        center: new naver.maps.LatLng(37.6342988, 127.2127402),
        zoom: 11, //기본줌
        minZoom: 2, //최소줌 
        zoomControl: true,//줌 조절 바 
        zoomControlOptions: // 줌 옵션 
        { //줌 컨트롤의 옵션
            position: naver.maps.Position.TOP_RIGHT
        }
    });

//var HOME_PATH = window.HOME_PATH || '.';

var Marker1 = new naver.maps.LatLng(37.6342988, 127.2127505),

    mark1 = new naver.maps.Marker({
        map: map,
        position: Marker1
    });

var Marker2 = new naver.maps.LatLng(37.6347071, 127.2101761),

    mark2 = new naver.maps.Marker({
        map: map,
        position: Marker2
    });

var Marker3 = new naver.maps.LatLng(37.636196, 127.2108617),

    mark3 = new naver.maps.Marker({
        map: map,
        position: Marker3
    });

var Marker4 = new naver.maps.LatLng(37.6366184, 127.2140564),

    mark4 = new naver.maps.Marker({
        map: map,
        position: Marker4
    });


var contentString = `
<div width="55" height="55">
    자전거 대여소<br>
    <a href="https://www.bikeseoul.com/" target="_blank">www.bikeseoul.com</a> <br>
    <button onclick='decideLocation()'>선택</button>
</div>
`;

var infowindow = new naver.maps.InfoWindow({
    content: contentString
});

naver.maps.Event.addListener(mark1, "click", function (e) {
    if (infowindow.getMap()) {
        infowindow.close();
    } else {
        infowindow.open(map, mark1);
    }
});

naver.maps.Event.addListener(mark2, "click", function (e) {
    if (infowindow.getMap()) {
        infowindow.close();
    } else {
        infowindow.open(map, mark2);
    }
});

function decideLocation(){
    let ajax = new XMLHttpRequest;

    vm.location = '어머머머'
    vm.numBike = 10
    vm.numUsedHelemt = 6
}