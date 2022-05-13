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

//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});

$(function () {
	let phone = $("#phone");
	let pass = $("#loginPassword");
	//初始化表单信息
	phone.val("");
	pass.val("");
	$("#messageCode").val("");
	$("#agree").prop("checked", false);

	//手机号码验证
	phone.blur(function () {
		let pVal = $.trim(phone.val());
		//    手机号不能为空
		if (pVal === "") {
			showError("phone", "手机号码不能为空");
			return;
		}

		//   手机号格式
		let resExp = /^1[1-9]\d{9}$/;
		let testRes = resExp.test(pVal);
		if (!testRes) {
			showError("phone", "手机号格式不正确");
			return;
		}

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
					showSuccess("phone");
				} else {
					showError("phone", data.message);
				}
			}
		})
	})

	pass.blur(function () {
		let passVal = $.trim(pass.val());
		// 密码不能为空
		if (passVal === "") {
			showError("loginPassword", "密码不能为空");
			return;
		}

		//    密码字符只可使用数字和大小写英文字母
		let resXep = /^[0-9a-zA-Z]+$/;
		if (!resXep.test(passVal)) {
			showError("loginPassword", "密码只能使用数字和大小写英文字母");
			return;
		}

		//    密码应同时包含英文或数字
		if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(passVal)) {
			showError("loginPassword", "密码应同时包含英文或数字");
			return;
		}

		//    密码应为 6-16 位
		if (!(passVal.length >= 6 && passVal.length <= 16)) {
			showError("loginPassword", "密码应为 6-16 位");
			return;
		}

		showSuccess("loginPassword");
	})

	//    验证短信验证码:不能为空
	$("#messageCodeBtn").click(function () {
		let $messageCodeBtn = $("#messageCodeBtn");

		//对输入的手机号和密码重新框进行验证
		phone.blur();
		pass.blur();

		//如果输入框没有错误则可以点击获取验证码按钮
		if ($("#loginPasswordErr").text() === "" && $("#phoneErr").text() === "") {
			//点击成功后，隐藏弹出的提示框
			hideError("messageCode");

			//发送ajax请求，获取验证码
			$.ajax({
				url: "/p2p/user/requestVerifyCode.do",
				data: {
					pVal:$("#phone").val()
				},
				dataType: "json",
				type: "post",
				success: function (data) {
					if (data.code === 1) {
						//点击按钮后倒计时
						if (!$messageCodeBtn.hasClass("on")) {
							$.leftTime(60, function (d) {
								if (d.status) {
									$messageCodeBtn.addClass("on");
									$messageCodeBtn.text(d.s === "00"? "60秒后重新发送":d.s + "秒后重新发送");
								} else {
									$messageCodeBtn.removeClass("on");
									$messageCodeBtn.text("获取验证码");
								}
							})
						}
					} else {
						showError("messageCode", data.message);
					}
				},
				error: function () {
					showError("messageCode", "发送验证码失败");
				}
			})
		}
	})

	$("#btnRegist").click(function () {
		//点击确定按钮的时候要再次确认手机号码和密码是否通过验证
		phone.blur();
		pass.blur();
		//点击确定按钮的时候要再次确认验证码是否为空
		let messageCode = $("#messageCode").val();

		if (messageCode === "") {
			showError("messageCode", "验证码不能为空");
		}

		//获取id是Err结尾的div元素的文本内容
		let errText = $("div[id$='Err']").text();

		if (errText === "") {
			let pVal = $.trim(phone.val());
			//对密码进行md5加密
			let passVal = $.md5($.trim(pass.val()));
			//加密用户输入的密码
			$("#loginPasswor").val(passVal);
			//获取用户输入的code
			let codeVal = $.trim($("#messageCode").val());

			$.ajax({
				url: "/p2p/user/register.do",
				data: {
					phone:pVal,
					pass:passVal,
					code:codeVal
				},
				dataType: "json",
				type: "post",
				success: function (data) {
					if (data.code === 1) {
						window.location.href = "/p2p/user/toRealNamePage.do";
					} else {
						//验证码输入错误，弹出错误信息后重新刷新图片验证码
						if (data.message === '验证码错误，请重新输入') {
							loadVerCodeImg();
						}
						showError('messageCode', data.message);
					}
				},
				//执行请求发生错误时
				error:function () {
					alert("注册失败，请稍后再试");
				}
			})
		}
	})
})

