var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode === 13){
		login();
	}
});

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

$(function () {
	let phone = $("#phone");
	let loginPassword = $("#loginPassword");
	let captcha = $("#captcha");

	phone.val("");
	loginPassword.val("");
	captcha.val("");

	loadVerCodeImg();

	phone.blur(function () {
		let pVal = phone.val();

		if (pVal === "") {
			showError("phone", "电话号码不能为空");
		} else if (!/^1[1-9]\d{9}$/.test(pVal)) {
			showError("phone", "电话号码格式错误");
		} else {
			//手机号是否已被注册
			$.ajax({
				url: "/p2p/user/verifyPhone.do",
				data: {
					phone:pVal
				},
				type: "get",
				dataType: "json",
				success:function (data) {
					if (data.code === 1) {
						showError("phone", "此用户不存在");
						showError("loginPassword", "密码错误");
					} else {
						showSuccess("phone");
					}
				}
			})
		}
	});

	loginPassword.blur(function () {
		let loginPassVal = loginPassword.val();

		if (loginPassVal === "") {
			showError("loginPassword", "密码不能为空");
		} else if (!/^[0-9a-zA-Z]+$/.test(loginPassVal)) {
			showError("loginPassword", "密码只能使用数字或英文字符");
		} else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassVal)) {
			showError("loginPassword", "密码应同时包含英文和数字");
		} else {
			showSuccess("loginPassword");
		}
	});

	captcha.blur(function () {
		let capVal = captcha.val();

		if (capVal === "") {
			showError("messageCode", "验证码不能为空");
		} else {
			hideError("messageCode");
		}
	})

	//点击图片刷新验证码信息
	$("#imgCode").click(function () {
		if ($("div[id$='Err']").text() === "") {
			loadVerCodeImg();
		}
	})

	$(document).keydown(function(event){
		if(event.keyCode === 13){
			login(phone, loginPassword, captcha);
		}
	});

	$("#loginBtn").click(function () {
		login(phone, loginPassword, captcha);
	})
})

function login(phone, loginPassword, captcha) {
	phone.blur();
	loginPassword.blur();

	captcha.blur();

	if ($("div[id$='Err']").text() === "") {
		let pVal = $.trim(phone.val());
		let loginPassVal = $.md5($.trim(loginPassword.val()));
		let capVal = $.trim(captcha.val());

		$.ajax({
			url: "/p2p/user/login.do",
			data: {
				phone:pVal,
				loginPassword:loginPassVal,
				captcha:capVal
			},
			dataType: "json",
			type: "post",
			success: function (data) {
				if (data.code === 1) {
					window.location.href = "/p2p/";
				} else {
					//验证码输入错误，弹出错误信息后重新刷新图片验证码，清空之前的内容，添加焦点
					if (data.message === '验证码错误，请重新输入') {
						loadVerCodeImg();
						captcha.val("");
						captcha.focus();
						showError('messageCode', data.message);
					} else if (data.message === '帐号或密码，请稍后再试') {
						showError('phone', data.message);
						showError('loginPassword', data.message);
					}

				}
			},
			error: function () {
				alert("系统异常，请稍后再试");
			}
		})
	}
}

function loadVerCodeImg() {
	$.ajax({
		url: "/p2p/user/requestVerifyCodeImage.do",
		dataType: "json",
		type: "get",
		success: function (data) {
			let verCodeImg = $("#imgCode");
			verCodeImg.attr("src", data.message);
		}
	})
}
