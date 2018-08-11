var vm = new Vue({
    el: '#app',
    data: {
        location: '',
        numBike: 10,
        numUsedHelemt: '',
    },
    method: {
        clickMap: function(){
            // ajax 로 spring 서버에 요청해서
            // location, numBike, 헬멧개수를 받아서
            // 대입만 시키려고
            this.location = '장소'
            this.numBike = 3
            this.numUsedHelemt = 2
        }
    }
});