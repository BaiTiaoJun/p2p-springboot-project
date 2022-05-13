
//同意实名认证协议
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
	let realName = $("#realName");
	let idCard = $("#idCard");
	let captcha = $("#captcha");

	phone.val("");
	realName.val("");
	idCard.val("");
	captcha.val("");
	$("#agree").prop("checked", false);

	//进入页面默认加载图片
	loadVerCodeImg();

	//手机号码验证
	phone.blur(function () {
		let pVal = $.trim(phone.val());

		if (pVal === "") {
			showError("phone", "手机号码不能为空");
		} else if (!/^1[1-9]\d{9}$/.test(pVal)) {
			showError("phone", "手机号码格式不正确");
		} else {
			showSuccess("phone");
		}
	})

	//姓名验证
	realName.blur(function () {
		let rNVal = $.trim(realName.val());

		if (rNVal === "") {
			showError("realName", "姓名不能为空");
		} else if (!/^[\u4e00-\u9fa5]{0,}$/.test(rNVal)) {
			showError("realName", "真实姓名只能输入中文");
		} else {
			showSuccess("realName");
		}
	})

	//身份证号验证
	idCard.blur(function () {
		let idCardVal = $.trim(idCard.val());

		if (idCardVal === "") {
			showError("idCard", "身份正号不能为空");
		} else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCardVal)) {
			showError("idCard", "身份证号格式错误");
		} else {
			showSuccess("idCard");
		}
	})

	captcha.blur(function () {
		let capVal = $.trim(captcha.val());

		if (capVal === "") {
			showError("captcha", "验证码信息不能为空")
		} else {
			hideError("captcha");
		}
	})

	// 点击图片更换图片
	$("#verCodeImg").click(function () {
		loadVerCodeImg();
	})

	// 认证按钮
	$("#btnRegist").click(function () {

		phone.blur();
		realName.blur();
		idCard.blur();
		captcha.blur();

		if ($("div[id$='Err']").text() === "") {
			$.ajax({
				url: "/p2p/user/verifyRealName.do",
				data: {
					phone:$.trim(phone.val()),
					realName:$.trim(realName.val()),
					idCard:$.trim(idCard.val()),
					captcha:$.trim(captcha.val())
				},
				dataType: "json",
				type: "post",
				success: function (data) {
					if (data.code === 1) {
						window.location.href = "/p2p/user/toMyCenterPage.do";
					} else {
						if (data.message === '验证码错误，请重新输入') {
							loadVerCodeImg();
							showError('captcha', data.message);
						} else if (data.message === '此手机号未注册，请重新输入') {
							showError('phone', data.message);
						} else if (data.message === '身份证号码或姓名有误，请重新验证') {
							showError('realName', data.message);
							showError('idCard', data.message);
						} else {
							alert(data.message);
						}
					}
				},
				error: function () {
					alert("系统异常，请稍后再试");
				}
			})
		}
	})
})

function loadVerCodeImg() {
	$.ajax({
		url: "/p2p/user/requestVerifyCodeImage.do",
		dataType: "json",
		type: "get",
		success: function (data) {
			let verCodeImg = $("#verCodeImg");
			verCodeImg.attr("src", data.message);
		}
	})
}