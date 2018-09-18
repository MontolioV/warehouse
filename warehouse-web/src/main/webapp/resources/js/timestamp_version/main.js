$(document).ready(function () {
    $('#scroller').click(function () {
        $('html, body').animate({scrollTop: 0}, 'fast');
    });

    $(window).scroll(function () {
        if (document.body.scrollTop > 250 || document.documentElement.scrollTop > 250) {
            document.getElementById('scroller').style.display = 'block';
        } else {
            document.getElementById('scroller').style.display = 'none';
        }
    });
});
