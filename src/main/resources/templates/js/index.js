
// 線上請求地址
// const URL = 'http://172.21.77.250:1314';
// 小田本地
const URL = 'http://172.21.76.18:1314';
// const URL = '';

// 只在正常模式时每 2 小时显示一次，每次显示 10s，后切换回正常模式
let countdown = 10;
// const dataList = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 0]
// (currentTime.getHours() % 2) === 0)


var CHANCE_SPEC, x, y;

CHANCE_SPEC = 60;
x = 0;
y = 0;

function rand(min, max) {
  return Math.floor(Math.random() * (max - min + 1) - min);
}

function drawSpec(data, x, y, w) {
  var index;

  index = (x + y * w) * 4;

  data.data[index + 0] = 0;
  data.data[index + 1] = 0;
  data.data[index + 2] = 0;
  data.data[index + 3] = 30;
}

function drawPattern(canvas, ctx, data) {
  var w, h;
  w = canvas.width;
  h = canvas.height;
  while (x < w) {
    if (rand(1, 100) < CHANCE_SPEC) {
      drawSpec(data, x, y, w);
    }
    x++;
  }
  if (x === w) {
    x = 0;
    y++;
  }
  if (y === h) {
    ctx.putImageData(data, 0, 0);
    return;
  }
  drawPattern(canvas, ctx, data);
}

function main() {
  var canvas, ctx, data;


  canvas = document.getElementById('my-canvas');
  ctx = canvas.getContext('2d');
  data = ctx.getImageData(0, 0, canvas.width, canvas.height);
  drawPattern(canvas, ctx, data);
  updateTime();
}

function updateTime() {

  var currentTime, seconds, minutes, hours, times, i;
  currentTime = new Date();

  (function getApi() {
    $.ajax({
      url: `${URL}/get_safe_time`,
      type: 'get',
      dataType: 'json',
      success: function (data) {
        const { data: resData } = data;
        times = {
          'day': resData[0].days,//顯示當前天數
          'hour': resData[0].hours,//當前小時
          'minute': resData[0].minutes,//當前分鐘
          'second': resData[0].seconds,//當前秒
        };
        $.each(resData, function (i, item) {
          if (item.state === "LIVE DANGER") {
            $("#clock-mode span").css({"color": "#ff0000","border":"1px solid #ff0000"}).text('LIVE DANGER，NEED HELP');
          }
          if (item.state === "PEATCH COMBAT") {
            $("#clock-mode span").css({"color": "#ff0","border":"1px solid #ff0"}).text('PEATCH COMBAT');
          }
          if (item.state === "PROBLEM COLSED") {
            // 判斷當前時間是否是整點
            const wholePoint = ((currentTime.getHours() % 2) === 0) && (currentTime.getMinutes() === 0) && (currentTime.getSeconds() < countdown);
            // const wholePoint = ((0 % 2) === 0) && (0 === 0) && (currentTime.getSeconds() < countdown);
            if (wholePoint) {
              times = {
                'day': currentTime.getHours(),
                'hour': 0,
                'minute': 0,
                'second': 0,
              };
              $("#clock-mode span").css({"color": "#9DF9A2","border":"1px solid #9DF9A2"}).text('LIVE LONGEST STABLE TIME');
            } else {
              $("#clock-mode span").css({"color": "#fff","border":"none"}).text('WAR CLOCK，LIVE STABLE');
            }
          }
        })
        for (type in times) {
          if (times.hasOwnProperty(type)) {
            setTimes(type, timeToString(times[type]));
          }
        }
      }
    })
  }())

  setTimeout(updateTime, 1000);
}

function timeToString(currentTime) {
  var t;
  t = currentTime.toString();
  if (t.length === 1) {
    return '0' + t;
  }
  return t;
}

function getPreviousTime(type) {
  return $('#' + type + '-top').text();
}

function setTimes(type, timeStr) {
  setTime(getPreviousTime(type + '-tens'),
    timeStr[0], type + '-tens');
  setTime(getPreviousTime(type + '-ones'),
    timeStr[1], type + '-ones');
}

function setTime(previousTime, newTime, type) {
  if (previousTime === newTime) {
    return;
  }
  setTimeout(function () {
    $('#' + type + '-top').text(newTime);
  }, 150);
  setTimeout(function () {
    $('.bottom-container',
      $('#' + type + '-bottom')).text(newTime);
  }, 365);
  animateTime(previousTime, newTime, type);
}

function animateTime(previousTime, newTime, type) {
  var top, bottom;
  top = $('#top-' + type + '-anim');
  bottom = $('#bottom-' + type + '-anim');
  $('.top-half-num', top).text(previousTime);
  $('.dropper', bottom).text(newTime);
  top.show();
  bottom.show();
  $('#top-' + type + '-anim').css('visibility', 'visible');
  $('#bottom-' + type + '-anim').css('visibility', 'visible');
  animateNumSwap(type);
  setTimeout(function () {
    hideNumSwap(type);
  }, 365);
}

function animateNumSwap(type) {
  $('#top-' + type + '-anim').toggleClass('up');
  $('#bottom-' + type + '-anim').toggleClass('down');
}

function hideNumSwap(type) {
  $('#top-' + type + '-anim').toggleClass('up');
  $('#bottom-' + type + '-anim').toggleClass('down');
  $('#top-' + type + '-anim').css('visibility', 'hidden');
  $('#bottom-' + type + '-anim').css('visibility', 'hidden');
}

$(document).ready(main);


window.requestAnimFrame = (function (callback) {
  return window.requestAnimationFrame ||
    window.webkitRequestAnimationFrame ||
    window.mozRequestAnimationFrame ||
    window.oRequestAnimationFrame ||
    window.msRequestAnimationFrame ||
    function (callback) { window.setTimeout(callback, 1000 / 60); }
})();
