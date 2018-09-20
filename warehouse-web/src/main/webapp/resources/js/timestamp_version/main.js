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

function copyTextToClipboard() {
    var text = document.getElementById('textContainer').innerHTML;
    function listener(e) {
        e.clipboardData.setData('text/html', text);
        e.clipboardData.setData('text/plain', text);
        e.preventDefault();
    }
    document.addEventListener('copy', listener);
    document.execCommand('copy');
    document.removeEventListener('copy', listener);
}

function convertDates() {
    $('.js-time-convert').each(function () {
        var dateString = $(this).text();
        var toLocalDate = moment(dateString, 'YYYY.MM.DD HH:mm:ss Z');
        $(this).text(toLocalDate.format('YYYY.MM.DD HH:mm:ss'));
        $(this).removeClass('js-time-convert');
    });
}