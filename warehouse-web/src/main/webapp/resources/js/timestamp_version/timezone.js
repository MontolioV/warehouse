$(document).ready(sendLocalTZToServer);

function sendLocalTZToServer() {
    var $localTimezone = $('#localTimezone');
    $localTimezone.val(moment.tz.guess());
    $localTimezone.change();
}