var vm = new Vue({
    el: '#app',
    data: {
        location: '',
        numBike: 1,
        numUsedHelemt: 1,
    },
    methods: {
        // clickMap: function(test){
        //     const USING_SERVER = 0
        //     const USING_TEST_ONE = 1

        //     // ajax 로 spring 서버에 요청해서
        //     // location, numBike, 헬멧개수를 받아서
        //     // 대입만 시키려고
        //     if(test == USING_SERVER){
        //         let ajax = new XMLHttpRequest();
        //     }
        //     else if(test == USING_TEST_ONE){
        //         this.location = '여의도 1번 출구 앞'
        //         this.numBike = 10
        //         this.numUsedHelemt = 6
        //     } else {
        //         this.location = '국제금융로 6길 앞'
        //         this.numBike = 20
        //         this.numUsedHelemt = 4
        //     }
        // }
    }
});