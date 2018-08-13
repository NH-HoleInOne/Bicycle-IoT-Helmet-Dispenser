var vm = new Vue({
    el: '#app',
    data: {
        location: '-',
        badHelmets: [
        ],
        helmetStorage: {
            good: 0,
            alcohol: 0,
            bad: 0,
        },
    },
    methods: {
        initialize(){
            location= '-'
            badHelmets= [
            ]
            helmetStorage= {
                good: 0,
                alcohol: 0,
                bad: 0,
            }
        }
    }
});