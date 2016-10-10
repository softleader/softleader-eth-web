/* ========== */
/* Button Up  */
/* ========== */

var upBtn = $('<div/>', { 'class': 'btnToTop' });

upBtn.appendTo('body');

$(document).on('click', '.btnToTop', scrollToTop);

function scrollToTop() {
	$('html, body').animate({
    	scrollTop: 0
    }, 500);
}

$(window).on('scroll', function() {
    if ($(this).scrollTop() > 100)
        $('.btnToTop').addClass('active');
    else
        $('.btnToTop').removeClass('active');
});