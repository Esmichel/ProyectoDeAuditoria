document.addEventListener("DOMContentLoaded", function() {
    // Get references to the form and the error message div
    const loginForm = document.getElementById("loginForm");
    const errorMessage = document.getElementById("error-message");

    loginForm.addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent the default form submission

        // Get the form data
        const formData = new FormData(loginForm);
        const username = formData.get("username");
        const password = formData.get("password");

        // Prepare the data to be sent in the request
        const data = {
            username: username,
            password: password
        };

        // Send the POST request to the API
        fetch("/api/auth/loginform", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Invalid username or password.");
            }
            return response.json();
        })
        .then(data => {
            // Handle success (e.g., store token, redirect user, etc.)
            console.log("Login successful:", data);
            // You can store the token or redirect here as needed
        })
        .catch(error => {
            // Display the error message if the login fails
            console.error("Error:", error);
            errorMessage.style.display = "block"; // Show the error message
        });
    });
});
