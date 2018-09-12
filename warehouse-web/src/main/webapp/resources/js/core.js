$(document).ready(sendLocalTZToServer);

function sendLocalTZToServer() {
    var $localTimezone = $('#localTimezone');
    $localTimezone.val(moment.tz.guess());
    $localTimezone.change();
}

function convertDates() {
    $('.js-time-convert').each(function () {
        var dateString = $(this).text();
        var toLocalDate = moment(dateString, 'YYYY.MM.DD HH:mm:ss Z');
        $(this).text(toLocalDate.format('YYYY.MM.DD HH:mm:ss'));
        $(this).removeClass('js-time-convert');
    });
}