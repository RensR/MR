$(function() {
    // add a click handler to the button
    $("#submitMood").click(function(event) {
        // make an ajax get request to get the message
        alert("Succes!");
        jsRoutes.controllers.MessageController.getMessage().ajax({
            success: function(data) {
                $(".wells").append($("<h1>").text(data.value));
            }
        });
    });
});