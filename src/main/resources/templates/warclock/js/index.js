
//假设每隔5秒发送一次请求
window.onload = function () {
  // 本地請求地址
  const URL = 'http://172.21.77.250:1314';
  // const URL = '';

  getApi();

  function getApi() {
    //设置时间 5-秒  1000-毫秒  这里设置你自己想要的时间
    setTimeout(getApi, 5 * 1000);
    $.ajax({
      url: `${URL}/get_safe_time`,
      type: 'get',
      dataType: 'json',
      success: function (data) {
        const { data: resData } = data;
        $("#time").empty();
        $.each(resData, function (i, item) {
          $("#time").append(
            "<li>" + item.days + "</li>" +
            "<li>" + item.hours + "</li>" +
            "<li>" + item.minutes + "</li>"
            /*"<li>" + item.state + "</li>"*/
          )
          if (item.state == "LIVE DANGER") {
            $("#denger").removeClass("hide")
            $("#warning").addClass("hide")
            $("#close").addClass("hide")
            $("#stable").addClass("hide")
          }
          if (item.state == "PEATCH COMBAT") {
            $("#denger").addClass("hide")
            $("#warning").removeClass("hide")
            $("#close").addClass("hide")
            $("#stable").addClass("hide")
          }
          if (item.state == "PROBLEM COLSED") {
            $("#denger").addClass("hide")
            $("#warning").addClass("hide")
            $("#close").addClass("hide")
            $("#stable").removeClass("hide")
          }
        })
      }
    })
  }






}