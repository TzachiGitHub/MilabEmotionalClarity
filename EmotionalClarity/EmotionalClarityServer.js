const express = require('express');

let app = express();

// Post request for sending token to server:
app.post('/:user/token', (req, res, next) => {
	// Get token from user:
    let token = req.body.token;
	if (!token) return res.status(400).json({err: "missing token"});
    console.log(`Received save token request from ${req.params.user} for token=${token}`);
	
	// Put it as a value in the JSON object 'tokens' with the user's username as key:
    tokens[req.params.user] = token;
    res.status(200).json({msg: "saved ok"});
});

app.listen(8080, () => {
    console.log("Started listening on 8080");
});