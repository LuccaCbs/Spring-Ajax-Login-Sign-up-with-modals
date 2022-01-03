$(document).ready(function () {

    //=======================
    //Ajax Post Sign-up Modal
    //=======================

    //The method to catch the "submit" event of the form
    $("#user-sign-up").submit(function (e) {
        //Prevents the form of being send by the default path
        e.preventDefault()
        //Calls the "signUp" function
        signUp();
    })

    function signUp() {
        //Creates a JSON object using the same paramethers that the "AppUser" has in the DB
        //Collect data from form inputs
        var formData = {
            name: $('#name').val(),
            dni: $('#dni').val(),
            mail: $('#mail').val(),
            phone: $('#phone').val(),
            address: $('#address').val(),
            password: $('#password').val(),
            role: $('#role').val(),
            active: $('#active').val()
        }

        //The JQuery ajax method
        $.ajax({
            //Specify the "content-type" thats going to be recived
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            //Specify the "method"
            type: "POST",
            //Specify the "content-type" thats being send by the method
            contentType: "aplication/json",
            //Specify the "url" thats going to recive the request
            url: "/saveClient",
            //Transform the form data into JSON format
            data: JSON.stringify(formData),
            //Specify the "dataType"
            dataType: "JSON",
            //Declares what sould be done if the request is successfully processed
            success: function (result) {
                console.log(result);
                if (result !== null) {
                    alert("Succesfully registered");
                    location.reload()
                } else {
                    alert("Error in register");
                }
            },
            //Declares what sould be done if the request is denied
            error: function (e) {
                $('#error').html(e.responseJSON.message);
                console.log(e);
            }
        })
    }

    //Same example but whit admin users, admin users apply security protocols
    $("#admin-sign-up").submit(function (e) {
        e.preventDefault()
        adminSignUp();
    })

    function adminSignUp() {
        var formData = {
            name: $('#name').val(),
            dni: $('#dni').val(),
            mail: $('#mail').val(),
            phone: $('#phone').val(),
            password: $('#password').val(),
            role: $('#role').val(),
            active: $('#active').val()
        }

        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            type: "POST",
            contentType: "aplication/json",
            url: "/saveAdmin",
            data: JSON.stringify(formData),
            dataType: "JSON",
            success: function (result) {
                console.log(result);
                if (result !== null) {
                    location.reload()
                } else {
                    alert("Error in register");
                }
            },
            error: function (e) {
                $('#error1').html(e.responseJSON.message);
                console.log(e);
            }
        })
    }


    //=====================
    //Ajax Post Login modal
    //=====================
    $("#user-login").submit(function (e) {
        e.preventDefault()
        login();
    })

    function login() {
        var formData = {
            username: $('#username').val(),
            password: $('#password1').val()
        }

        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            type: "POST",
            contentType: "aplication/json",
            url: "/login",
            data: JSON.stringify(formData),
            dataType: "JSON",
            success: function (result) {
                if (result.status == true) {
                    //Reloads the page if the login was successfully processed
                    location.reload()
                } else {
                    console.log(result);
                    $('#error2').html(result.error);
                }
            },
            error: function (e) {
                console.log(e);
            }
        })
    }

});