
$(function () {
//	getVerifyCode($("#name").val());
	hpHeight();
	fhEventListen(window, 'resize', hpHeight);
	
	$("#loginForm").validate({
		rules : {
			username : "required",
			password : "required",
			code : "required"
		},
		messages : {
			username : '请填写登录名',
			password : '请填写密码',
			code : '请填写验证码'
		},
		errorPlacement : function(error, element) {
			$(element).closest(".form-group").find(".error").append(error);
		}
	});
	
	
	
	$("#btn").click(function () {
		getVerifyCode($("#username").val());
	});
});

function getVerifyCode(username) {
	var data = {
			"username" : username
		}
	
	$.ajax({
		global : false,//禁用全局ajax事件
		method : 'post',
		url : ctx + '/verifyCode',
		data : data,
		success : function(data){
			if (data.success == true || data.success == "true") {
				let count = 60;
			    const countDown = setInterval(() => {
			      if (count === 0) {
			       $('#btn').val('重新发送').removeAttr('disabled');
			       $('#btn').css({
			        background: '#449d44',
			        color: '#fff',
			       });
			       clearInterval(countDown);
			      } else {
			       $('#btn').attr('disabled', true);
			       $('#btn').css({
			        background: '#d8d8d8',
			        color: '#707070',
			       });
			       $('#btn').val(count + '秒后可重发');
			      }
			      count--;
			     }, 1000);
			} else {
//				console.log(1)
				$("#notice").show();
				$("#warnning").html(data.message);
			}
		},
		error : function(data) {
			$("#submit").html('请求异常');//.removeClass("disabled");
		}
	})
	//$("#verifyCodeImage").attr("src", ctx + "/verifyCode?j_username=" + username);
}

function hpHeight(){
	var bh = window.innerHeight || document.documentElement.clientHeight;
	var bw = window.innerWidth || document.documentElement.clientWidth;
	document.getElementById('loginHeight').style.height = bh + 'px';
	document.getElementById('loginHeight').style.textAlign = 'center';
}

function fhEventListen(oTarget, sType, fListener, bUseCapture){
	bUseCapture = !!bUseCapture;
	if (oTarget.addEventListener){
		oTarget.addEventListener(sType, fListener, bUseCapture); //Mozilla
	}else if(oTarget.attachEvent){
		oTarget.attachEvent('on' + sType, fListener);  //IE事件前加“on”
	}
}