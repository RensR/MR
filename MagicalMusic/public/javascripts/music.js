$(document).ready(function()
{
     $('#youtube').hide();
});

$(function() {
    // add a click handler to the button
    $("#submitMood").click(function(event) {
        // make an ajax get request to get the message
       // alert("Succes!");
        jsRoutes.controllers.MessageController.getSong($('#inputMood').val()).ajax({
            success: function(data) {
                //data is the result of the algorithm.
                //do something with data:
                $('#wells').append(data)
                
                //show the song in a YouTube player:
                youtube = document.getElementById('youtube');
                youtube.src = "http://www.youtube.com/embed/" + data.value + "?autoplay=1";
                $(youtube).show();
                introtext = document.getElementById('intro-text');
                $(introtext).hide();
            }
        });
    });
});