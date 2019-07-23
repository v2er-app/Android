function addClickToImg() {
    var imgs = document.getElementsByTagName("img");
    var urls = new Array();
    for (var i = 0; i < imgs.length; i++) {
        const index = i;
        urls[i] = imgs[i].getAttribute('original_src');
        imgs[i].onclick = function () {
           window.imagelistener.openImage(index, urls);
        };
    }
}

function reloadImg(original_img, localPath) {
//    console.error(localPath);
    var imgs = document.querySelectorAll("*[original_src='" + original_img + "']");
    for (var i=0; i<imgs.length; i++) {
        console.error(imgs[i]);
        imgs[i].setAttribute("src", localPath);
    }
}

function injectOnLoad(isDark, fontSize) {
    decodeEmail();
    document.body.className += isDark ? 'dark ' : ' ';
    document.body.style.fontSize = fontSize;
}
