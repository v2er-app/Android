function addClickToImg() {
    var imgs = document.getElementsByTagName("img");
    var urls = new Array();
    for (var i = 0; i < imgs.length; i++) {
        const index = i;
        urls[i] = imgs[i].src;
        imgs[i].onclick = function () {
//            alert("index: " + index + ", sum: " + imgs.length);
              window.imagelistener.openImage(index, urls);
        };
    }
}

