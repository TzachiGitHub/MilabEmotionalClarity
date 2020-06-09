const functions = require('firebase-functions');
const express = require('express');
const request = require('request');
const bodyParser = require('body-parser');
const HashMap = require('hashmap');
const functions = require(‘firebase-functions’);

const FCM = require('fcm-push');
const serviceKey = 'AAAA0LmF77M:APA91bH0jtYlutZ4YI_gKQ8ABeHI9FMHG0S_WvEzffDs2ajOWEOoQ-39AfPf1HJ9bOAJKmeZklOaX0JZmeWBEl57kNWKoa1LSlt7Lh9bJ9UmcxsHo8LSRfLOhfOyxgz0R9v_CshoSuNj';

const pd = require('paralleldots');
const ToneAnalyzerV3 = require('ibm-watson/tone-analyzer/v3');

const { IamAuthenticator } = require('ibm-watson/auth');
pd.apiKey = "flOZwHEMA0rpiKiKjGwUSvjK66Zj4mhtCrreSdTrgoQ";

let fcm = new FCM(serviceKey);
let app = express();
app.use(bodyParser.json());

let tokens = {}; // To keep device tokens

// Map that will match keys from Watson and PD responses to globally identical values:
let emotionsMap = new HashMap();
emotionsMap.set("Happy", "Happiness");
emotionsMap.set("Joy", "Happiness");
emotionsMap.set("Fear", "Fear");
emotionsMap.set("Anger", "Anger");
emotionsMap.set("Angry", "Anger");
emotionsMap.set("Sadness", "Sadness");
emotionsMap.set("Sad", "Sadness");
emotionsMap.set("Bored", "Boredom");
emotionsMap.set("Excited", "Excitement");
emotionsMap.set("Confident", "Confidence");
emotionsMap.set("Analytical", "Confidence");
emotionsMap.set("Tentative", "Insecurity");

pdResponse = false;
watsonResponse = false;


// Activate Watson API:
const toneAnalyzer = new ToneAnalyzerV3({
	authenticator: new IamAuthenticator({apikey: 'jPFfKyMLK1zyPyk4vg5IHSFtBVPR-0x_mQv9o27d1zot'}),
	version: '2017-09-21',
	url: 'https://api.eu-gb.tone-analyzer.watson.cloud.ibm.com/instances/62c6728e-ead3-46ce-b20c-b718e0482c4d'
});

// Post request for sending token to server:
app.post('/token', (req, res, next) => {
	// Get token from user:
    let token = req.body.token;
	if (!token) return res.status(400).json({err: "missing token"});
    console.log(`Received save token request from ${req.params.user} for token=${token}`);
	
	// Put it as a value in the JSON object 'tokens' with the user's username as key:
    tokens[req.params.user] = token;
    res.status(200).json({msg: "saved ok"});
});

app.post('/analyze', (req, res, next) => {
	let promises = [] // To resolve callbacks
	
	let scoresMap = {
		"Happiness": 0,
		"Fear": 0,
		"Anger": 0,
		"Sadness": 0,
		"Boredom": 0,
		"Excitement": 0,
		"Confidence": 0,
		"Insecurity": 0
	};
	
	let appearancesMap = {
		"Happiness": 0,
		"Fear": 0,
		"Anger": 0,
		"Sadness": 0,
		"Boredom": 0,
		"Excitement": 0,
		"Confidence": 0,
		"Insecurity": 0
	};
	
	let watsonJSON;
	let pdJSON;
	let resultJSON ={};
	
	// Get user input:
	let text = req.body.text;
	if (!text) return res.status(400).json({err: "no text"});
	
	promises.push(new Promise((resolve, reject) => {
		/*////////////////////////
		// Paralleldots analysis:
		////////////////////////*/
		console.log(text);
		/* Send the request: */
		pd.emotion(text, "en").then((response) => {
			pdJSON = {};
			let tones = (JSON.parse(response))["emotion"];
			for(tone in tones) {
				let currentScore = scoresMap[emotionsMap.get(tone)];
				scoresMap[emotionsMap.get(tone)] = currentScore + tones[tone];
				
				let currentAppearances = appearancesMap[emotionsMap.get(tone)];
				appearancesMap[emotionsMap.get(tone)] = currentAppearances + 1;
				
				pdJSON[emotionsMap.get(tone)] = tones[tone];
			}
			resolve();
			return null;
			
		}).catch((error) => {
			console.log(error);
			reject(error);
		})
	}));
	
	promises.push(new Promise((resolve, reject) => {
		/*////////////////////////
		// IBM Watson analysis:
		////////////////////////*/
		
		/* Insert the user's text as parameter to send in the request to the Watson API: */
		const toneParams = {
			toneInput: text,
			contentType: 'text/plain',
		};
		
		/* Send the request: */
		toneAnalyzer.tone(toneParams).then(toneAnalysis => {
			watsonJSON ={};
			let tones = toneAnalysis["result"]["document_tone"]["tones"];
			tones.forEach(function(tone) {
				let currentAppearances = appearancesMap[emotionsMap.get(tone.tone_name)];
				appearancesMap[emotionsMap.get(tone.tone_name)] = currentAppearances + 1;
				
				/* Calculate the average of the 2 response scores: */
				let currentScore = scoresMap[emotionsMap.get(tone.tone_name)];
				scoresMap[emotionsMap.get(tone.tone_name)] = (currentScore + tone.score)/(currentAppearances + 1.0);
				watsonJSON[emotionsMap.get(tone.tone_name)] = tone.score;
			});

			
			for(var tone in scoresMap){
				resultJSON[tone] = scoresMap[tone];
			}
			
			resolve();
			return null;
		}).catch(err => {
			console.log('error:', err);
			reject(err);
		});
	}));
	
	Promise.all(promises).then(()=> {
		return res.status(200).json({result: resultJSON});
	}).catch(() => null);
});

app.get('/sayhello', (req, res) => {
	res.send("HI");
});


app.listen(8080, () => {
    console.log("Started listening on 8080");
});

exports.app = functions.https.onRequest(app);

