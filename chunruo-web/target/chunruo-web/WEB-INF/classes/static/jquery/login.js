
$(function () {
//	getVerifyCode($("#name").val());
	hpHeight();
	fhEventListen(window, 'resize', hpHeight);
	
	$("#loginForm").validate({
		rules : {
			name : "required",
			password : "required",
			code : "required"
		},
		messages : {
			name : '请填写登录名',
			password : '请填写密码',
			code : '请填写验证码'
		},
		errorPlacement : function(error, element) {
			$(element).closest(".form-group").find(".error").append(error);
		}
	})
	
	//监听登录按钮点击事件
	$("#submit").on("click", function() {
		if ($("#loginForm").valid()) {
			$("#submit").html('请求中……').addClass("disabled");
			var exponent = $("#exponent").val();
			var modulus = $("#modulus").val();
		    var orgPwd = $("#password").val();
		    var rememberMe = $("#rememberMe").is(':checked');
		    
		    RSAUtils.setMaxDigits(200);
		    //var key = new RSAUtils.getKeyPair(exponent, "", modulus);  
		    //var encrypedPwd = RSAUtils.encryptedString(key, orgPwd);
			var data = {
				"username" : $("#username").val(),
				"password": orgPwd,
				"code" : $("#code").val(),
				"remember-me":rememberMe
			}
			
			$.ajax({
				global : false,//禁用全局ajax事件
				method : 'post',
				url : ctx + '/j_security_check',
				data : data,
				success : function(data){
					console.log(data);
					if (data.model.success == true || data.model.success == "true") {
						window.location.href = ctx + data.model.data;
						$("#submit").html('验证通过，请等待页面转……');
					} else {
						$("#submit").html(data.model.message);
						$("#exponent").val(data.model.publicKeyExponent);
						$("#modulus").val(data.model.publicKeyModulus);
//						getVerifyCode();
						setTimeout(function(){
							$("#submit").html('登&nbsp;录').removeClass("disabled");
						},3000);
					}
				},
				error : function(data) {
					$("#submit").html('请求异常');//.removeClass("disabled");
				}
			})
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
//			console.log(1)
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
				$("#submit").html(data.message);
				setTimeout(function(){
					$("#submit").html('登&nbsp;录').removeClass("disabled");
				},3000);
//				$("#notice").show();
//				$("#warnning").html(data.message);
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