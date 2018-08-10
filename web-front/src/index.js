var vm = new Vue({
    el: '#app',
    data: {
        location: '',
        numBike: '',
        numUsedHelemt: '',
    },
    method: {
        clickMap: function(event){
            alert('location clicked.');
            console.log('ahahahah');
            this.location = '왜또';
        }
    }
});