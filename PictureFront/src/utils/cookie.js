export default{
setCookie: function(c_name, c_pwd, exdays){
    var exdate = new Date(); //获取时间
    exdate.setTime(exdate.getTime() + 24 * 60 * 60 * 1000 * exdays); //保存的天数
    //字符串拼接cookie
    window.document.cookie = "userName" + "=" + c_name + ";path=/;expires=" + exdate.toGMTString();
    window.document.cookie = "userPwd" + "=" + c_pwd + ";path=/;expires=" + exdate.toGMTString();
}
,
//读取cookie

getCookie: function(){
    var user = ['userName','userPwd'];

    if (document.cookie.length > 0) {
        var arr = document.cookie.split('; '); //这里显示的格式需要切割一下自己可输出看下

        
        for (var i = 0; i < arr.length; i++) {
            var arr2 = arr[i].split('='); //再次切割
           
            //判断查找相对应的值
            if (arr2[0] == 'userName') {
                user[0] = arr2[1]; //保存到保存数据的地方
            } else if (arr2[0] == 'userPwd') {
                user[1] = arr2[1];
            }
        }
    }

  return  user; 
}
,
//清除cookie
clearCookie: function(){
    this.setCookie("ss", "", -1); //修改2值都为空，天数为负1天就好了
}
}