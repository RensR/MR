$(document).ready(function()
{
     $('#youtube').hide();
     $('#inputMoodValue').hide();

    //emoticons:        
        
        var $wysiwyg = $('#inputMood').emojiarea({wysiwyg: true});
        var $wysiwyg_value = $('#inputMoodValue');
        
        $wysiwyg.on('change', function() {
            $wysiwyg_value.text($(this).val());
        });
        $wysiwyg.trigger('change');
});

$(function() {
    // add a click handler to the button
    $("#submitMood").click(function(event) {
        // make an ajax get request to get the message
       // alert("Succes!");
        jsRoutes.controllers.MessageController.getSong($('#inputMood').val()).ajax({
            success: function(data) {
                //data is the result of the algorithm.
                //show the song in a YouTube player:
                youtube = document.getElementById('youtube');
                if(data.value != "No results.")
                {
                    youtube.src = "http://www.youtube.com/embed/" + data.value + "?autoplay=1";
                    $(youtube).show();
                    introtext = document.getElementById('intro-text');
                    $(introtext).hide();
                }
                else
                {
                    introtext = document.getElementById('intro-text');
                    introtext.innerHTML = "<h1>No results found</h1>";
                }
            }
        });
    });
});