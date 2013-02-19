//Make a <span id="liveclock"> in the HTML code
var Digital=new Date();
var time = Digital.getTime();
function newClock(stringHora) {
 Digital.setHours(stringHora.substring(0,2));
 Digital.setMinutes(stringHora.substring(3,5));
 Digital.setSeconds(stringHora.substring(6,8));
 time = Digital.getTime();
 showClock();
}

function showClock() {
 if (!document.getElementById('liveclock')) return;
 time += 1000;
 Digital.setTime(time);
 var hours=Digital.getHours();
 var minutes=Digital.getMinutes();
 var seconds=Digital.getSeconds();
 if (minutes<=9)
 minutes="0"+minutes;
 if (seconds<=9)
 seconds="0"+seconds;
 myclock=hours+":"+minutes+":"+seconds;
 document.getElementById('liveclock').innerHTML=myclock;
 setTimeout("showClock()",1000);
}