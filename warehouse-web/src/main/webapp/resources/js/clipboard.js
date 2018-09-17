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