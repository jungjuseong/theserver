<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="../inc/top.jsp" %>
<script type="text/javascript" src="/js/jquery.form.mini.js" ></script>
<script type="text/javascript" src="/js/jquery.validate.js"></script>
<script type="text/javascript" src="/js/jquery-dateFormat.js"></script>
<script>


$(document).ready(function(){
	<sec:authorize access="isAuthenticated()">  
		<sec:authentication property="principal.authorities" var="userRole" />
	</sec:authorize>
	

	$("#appSearch").click(function(){
		 window.open("/contents/write/popUpContents.html","","width=680, height=333, top=100, left=100, resizable= yes");
		 /* frm.target  = target_;
		 frm.method  = "get";
		 frm.action  = "/contents/write/popUpContents.html";
		 frm.submit(); */
	});

	//수정 완료후 alert
	if("<c:out value='${modifySuccess}'/>" == "true"){
		//message : 수정되었습니다.
		alert("<spring:message code='contents.modify.032' />");
	}

	$('[name=relatedAppName], #appSearch').focus(function(){
		$('#appSearch').focus().click();
	});

	$("#contentsFile").click(function (e){
		if("<c:out value='${userRole}'/>" == "[ROLE_ADMIN_SERVICE]"){
			e.preventDefault();
		}else{
		}
	});

	$("#contentsFile").change(function (){
		
		if($('.removeImgBtn2').length > 0 ) {
			$("#contentsFile").val("");
			//message : 이미 파일이 존재합니다.
			alert("<spring:message code='contents.modify.034' />")
		}else{
			//확장자명 구하기
			var filename = $(this).val();
			var dot = filename.lastIndexOf(".");
			var ext = filename.substring(dot+1).toLowerCase();
			
			//파일이름 구하기
			var slash = filename.lastIndexOf("\\");
			var name = filename.substring(slash+1);
			
			
			switch ($("#contentsType").val()) {
			  case '1'  : if(!(ext=='html' || ext == 'zip')){
				  			$(this).val('');
				  			//message : HTML, zip 파일 형식이 필요합니다.
							alert("<spring:message code='contents.modify.035' />");
						}else{
							contentsFileAppend(name);
						}
			  		break;
			  case '2'  : if(!(ext=='epub')){
						    $(this).val('');
						    //message : ePub 파일 형식이 필요합니다.
							alert("<spring:message code='contents.modify.036' />");
						}else{
							contentsFileAppend(name);
						}
			        break;
			  case '3'  : if(!(ext=='pdf')){
							$(this).val('');
							//message : pdf 파일 형식이 필요합니다.
							alert("<spring:message code='contents.modify.037' />");
						}else{
							contentsFileAppend(name);
						}
			  		break;
			}
		}
	});

	//파일 삭제
	 $(document).on('click', '.removeImgBtn2', function(){
		$("#uploadInput").empty();
		
		/* 파일이 바뀐다는 플래그 */
		$("#fileChanged").attr('value','1');
	});

	$("#modifyBtn").click(function(){
		if((!$('.removeImgBtn2').length > 0) && (!$('#contentsFile').val().length >0) ){
			//message : 파일을 넣어주십시오.
			alert("<spring:message code='contents.modify.038' />");
			$("#contentsFile").focus();
		}else{
			$("#couponNum").attr("disabled", false);
			$("#contents_modify_f").submit();
		}
	});

	$("#d_EDATE").mousedown(function(e){
		if($("#d_SDATE").val() == null || $("#d_SDATE").val() == ""){
			//message : 시작 날짜를 입력해주십시오.
			alert("<spring:message code='contents.modify.039' />");
			e.preventDefault();
		} 
		
	});

	$("#c_EDATE").mousedown(function(e){
		if($("#c_SDATE").val() == null || $("#c_SDATE").val() == ""){
			//message : 시작 날짜를 입력해주십시오.
			alert("<spring:message code='contents.modify.039' />");
			 e.preventDefault(); 
		} 
	});

	$("#c_SDATE").change( function(){

		//현재 시간
		var d = new Date();
		var curr_year = d.getFullYear();
		var curr_month = d.getMonth();
		var curr_date = d.getDate();
		var curr_hour = d.getHours();
		var curr_min = d.getMinutes();

		//HTML에서 가져온 날자 값
		var inputDate = $("#d_SDATE").val();
		var split = inputDate.split("-");

		//입력된 시간
		var inputYear = split[0];
		var inputmonth = split[1];
		var inputdate = split[2];
		curr_month += 1

		//월 Reformat
		if(curr_month < 10){
			curr_month = "0"+curr_month;
		}

		//Date Reformat
		if(curr_date < 10){
			curr_date = "0"+curr_date
		}
	});

	$("#d_SDATE").change( function(){

		//현재 시간
		var d = new Date();
		var curr_year = d.getFullYear();
		var curr_month = d.getMonth();
		var curr_date = d.getDate();
		var curr_hour = d.getHours();
		var curr_min = d.getMinutes();

		//HTML에서 가져온 날자 값
		var inputDate = $("#d_SDATE").val();
		var split = inputDate.split("-");

		//입력된 시간
		var inputYear = split[0];
		var inputmonth = split[1];
		var inputdate = split[2];
		curr_month += 1

		//월 Reformat
		if(curr_month < 10){
			curr_month = "0"+curr_month;
		}

		//Date Reformat
		if(curr_date < 10){
			curr_date = "0"+curr_date
		}
	});

	$('.date').keydown(function(e) {
		   e.preventDefault();
		   return false;
	});

	$("#contents_modify_f").validate({
	    rules: {
	    	
	    	contentsName : {
	    		required : true,
	    		maxlength : 50
	    	},
	    	
	    	verNum : {
	    		required : true,
	    		maxlength : 10
	    	},

	    	memDownAmt: {
	    		required : true,
	    		maxlength : 20
	    	},
	    	
	    	memDownStartDt : {
	    		required : true,
	    	},
	    	
	    	memDownEndDt : {
	    		required : true,
	    	},
	    	
	    	nonmemDownAmt: {
	    		required : true,
	    		maxlength : 20,
	    	},
	    	
	    	nonmemDownStarDt : {
	    		required : true,
	    	},
	    	
	    	nonmemDownEndDt : {
	    		required : true,
	    	},
	    },

	    messages : {

	    	//message : 콘텐츠 이름을 입력해 주십시오.	
	    	contentsName : {
	    		required : "<spring:message code='contents.modify.044' />",
	    		maxlength : "50<spring:message code='extend.local.060' />"
	    	},

	    	//message : 버전을 입력해 주십시오.
	    	verNum : {
	    		required : "<spring:message code='contents.modify.045' />",
	    		maxlength : "10<spring:message code='extend.local.060' />"
	    	},

	    	memDownAmt : {
	    		required : "",
	    		maxlength : "20<spring:message code='extend.local.060' />"
	    	},

			memDownStartDt : {
				required : "",
			},

			memDownEndDt : {
				required : "",
			},

	    	nonmemDownAmt : {
	    		required : "",
	    		maxlength : "20<spring:message code='extend.local.060' />"
	    	},

	    	nonmemDownStarDt : {
	    		required : "",
	    	},

	    	nonmemDownEndDt : {
	    		required : "",
	    	},
		},

		errorPlacement : function(error, element) {
			error.appendTo( element.parent("td, div") );
		}
	});

	if($("[name = completGb]:checked").val() == '2'){
		$("[name = distrGb]").attr('disabled', true);
		$("[name = memDownGb]").attr('disabled', true);
		$("[name = memDownAmt]").attr('readonly', true);
		$("[name = couponGb]").attr('disabled', true);
		$("[name = couponNum]").val("");
		$("[name = couponNum]").attr('readonly', true);
		$("[name = nonmemDownGb]").attr('disabled', true);
		$("[name = nonmemDownAmt]").attr('readonly', true);
		
		$("[name = memDownStartDt]").attr('disabled', true);
		$("[name = memDownStartDt]").attr('readonly', true);
		$("[name = memDownEndDt]").attr('disabled', true);
		$("[name = memDownEndDt]").attr('readonly', true);
		
		$("[name = nonmemDownStarDt]").attr('readonly', true);
		$("[name = nonmemDownStarDt]").attr('disabled', true);
		$("[name = nonmemDownEndDt]").attr('readonly', true);
		$("[name = nonmemDownEndDt]").attr('disabled', true);
	}

	/* completFunction();
	distrFunction(); */
	
	$("[name = completGb]").change( function(){
		completFunction();
	});
	
	$("[name = memDownGb]").click(function(){
		memDownFunction();
	});
	
	$("[name = distrGb]").change(function(){
		distrFunction();
	});
	
	$("[name = couponGb]").change(function(){
		couponFunction();
	});
	
	$("[name = nonmemDownGb]").change(function(){
		nonMemFunction();
	});
	
	initialize();
});










function contentsFileAppend( fileName ){
	if($("#contentsFile").val() != ""){
		/* $("#contentsFile").val(""); */
/* 		var html= "";
		html += "<span id='uploadOrgName'>" + fileName +"</span>";
		html += "<a class='removeImgBtn2' href='#'><img src='/images/btn_close_s.png' alt='아이콘 썸네일 이미지 닫기'></a>"
		$("#uploadInput").append(html);  */
		/* $("fileChanged").val("1"); */
	}
}


function completFunction(){
	if($("[name = completGb]:checked").val() == '1'){

		$("[name = distrGb]").attr('disabled', false);
		$("#m1").prop("checked", true);	/* 회원 로그인 Checked */	
		$("[name = memDownGb]").attr('disabled', false);
		$("#d1").prop("checked", true);	/* 다운로드 횟수 Checked*/
		$("#d1").prop("checked", true);	/* 다운로드 횟수 Checked*/
		$("[name = memDownAmt]").attr('readonly', false);
		$("[name = couponGb]").attr('disabled', false);
		$("#cou_n").prop("checked", true);	/* 쿠폰 사용 여부 = '아니오' Checked*/

		$("[name = memDownStartDt]").attr('disabled', true);
		$("[name = memDownStartDt]").attr('readonly', true);
		$("[name = memDownEndDt]").attr('disabled', true);
		$("[name = memDownEndDt]").attr('readonly', true);
		
		$("[name = couponNum]").attr('disabled', true);
		$("[name = nonmemDownGb]").attr('disabled', true);
		$("[name = nonmemDownAmt]").attr('readonly', true);
		$("[name = nonmemDownStarDt]").attr('readonly', true);
		$("[name = nonmemDownStarDt]").attr('disabled', true);
		$("[name = nonmemDownEndDt]").attr('readonly', true);
		$("[name = nonmemDownEndDt]").attr('disabled', true);
	}else if($("[name = completGb]:checked").val() == '2'){
		$("[name = distrGb]").attr('disabled', true);
		$("[name = distrGb]").attr('checked', false);
		$("[name = memDownGb]").attr('disabled', true);
		$("[name = memDownGb]").attr('checked', false);
		$("[name = memDownAmt]").attr('readonly', true);
		$("[name = memDownAmt]").val("");
		$("[name = couponGb]").attr('disabled', true);
		$("[name = couponGb]").attr('checked', false);
		$("[name = couponNum]").attr('readonly', true);
		$("[name = nonmemDownGb]").attr('disabled', true);
		$("[name = nonmemDownGb]").attr('checked', false);
		$("[name = nonmemDownAmt]").attr('readonly', true);
		$("[name = nonmemDownAmt]").val("");
		$("[name = memDownStartDt]").attr('disabled', true);
		$("[name = memDownStartDt]").attr('readonly', true);
		$("[name = memDownStartDt]").val("");
		$("[name = memDownEndDt]").attr('disabled', true);
		$("[name = memDownEndDt]").attr('readonly', true);
		$("[name = memDownEndDt]").val("");
		$("[name = nonmemDownStarDt]").attr('readonly', true);
		$("[name = nonmemDownStarDt]").attr('disabled', true);
		$("[name = nonmemDownStarDt]").val("");
		$("[name = nonmemDownEndDt]").attr('readonly', true);
		$("[name = nonmemDownEndDt]").attr('disabled', true);
		$("[name = nonmemDownEndDt]").val("");
	}
}

function memDownFunction(){
	if($("[name = memDownGb]:checked").val() == '1'){

		$("[name = memDownStartDt]").attr('disabled', true);
		$("[name = memDownStartDt]").attr('readonly', true);
		$("[name = memDownStartDt]").val("");
		$("[name = memDownEndDt]").attr('disabled', true);
		$("[name = memDownEndDt]").attr('readonly', true);
		$("[name = memDownEndDt]").val("");
		$("[name = memDownAmt]").attr('readonly', false);
		$("[name = memDownAmt]").val("");
	}else if($("[name = memDownGb]:checked").val() == '2'){

		$("[name = memDownAmt]").attr('readonly', true);
		$("[name = memDownAmt]").val("");
		$("[name = memDownStartDt]").attr('disabled', false);
		$("[name = memDownStartDt]").attr('readonly', false);
		$("[name = memDownStartDt]").val("");
		$("[name = memDownEndDt]").attr('disabled', false);
		$("[name = memDownEndDt]").attr('readonly', false);
		$("[name = memDownEndDt]").val("");

	}else{
		$("[name = memDownAmt]").attr('readonly', true);
		$("[name = memDownAmt]").val("");
		$("[name = memDownStartDt]").attr('disabled', true);
		$("[name = memDownStartDt]").attr('readonly', true);
		$("[name = memDownStartDt]").val("");
		$("[name = memDownEndDt]").attr('disabled', true);
		$("[name = memDownEndDt]").attr('readonly', true);
		$("[name = memDownEndDt]").val("");
	}
}

function distrFunction(){
	if($("[name = distrGb]:checked").val() == '1'){
		
		if($("[name = memDownGb]:checked").val() == '2'){
			
		}else{
			$("#d1").prop('checked', true);
			$("[name = memDownGb]").attr('disabled', false);
			
			$("[name = memDownAmt]").attr('readonly', false);
			$("[name = memDownAmt]").attr('disabled', false);
		}
		/*$("#d2").attr('checked', true); */
	}else if($("[name = distrGb]:checked").val() == '2' && $("[name = couponGb]:checked").val() == '1'){
		$("[name = memDownGb]").attr('disabled',true);
		$("[name = memDownGb]").attr("checked", false);
		$("[name = memDownAmt]").val("");
		$("[name = memDownAmt]").attr("readonly", true);
		$("[name = memDownStartDt]").attr('disabled', true);
		$("[name = memDownStartDt]").attr('readonly', true);
		$("[name = memDownEndDt]").attr('disabled', true);
		$("[name = memDownEndDt]").attr('readonly', true);
		$("[name = memDownStartDt]").val("");
		$("[name = memDownEndDt]").val("");
		/*$("#d2").attr('checked', true); */
	}
}


function nonMemFunction(){
	if($("[name = nonmemDownGb]:checked").val() == '1'){
		$("[name = nonmemDownStarDt]").attr('disabled', true);
		$("[name = nonmemDownStarDt]").attr('readonly', true);
		$("[name = nonmemDownStarDt]").val("");
		$("[name = nonmemDownEndDt]").attr('disabled', true);
		$("[name = nonmemDownEndDt]").attr('readonly', true);
		$("[name = nonmemDownEndDt]").val("");
		$("[name = nonmemDownAmt]").attr('readonly', false);
		$("[name = nonmemDownAmt]").val("");
	}else if($("[name = nonmemDownGb]:checked").val() == '2'){
		$("[name = nonmemDownAmt]").attr('readonly', true);
		$("[name = nonmemDownAmt]").val("");
		$("[name = nonmemDownStarDt]").attr('disabled', false);
		$("[name = nonmemDownStarDt]").attr('readonly', false);
		$("[name = nonmemDownStarDt]").val("");
		$("[name = nonmemDownEndDt]").attr('disabled', false);
		$("[name = nonmemDownEndDt]").attr('readonly', false);
		$("[name = nonmemDownEndDt]").val("");
	}else{
		$("[name = nonmemDownStarDt]").attr('disabled', true);
		$("[name = nonmemDownStarDt]").attr('readonly', true);
		$("[name = nonmemDownStarDt]").val("");
		$("[name = nonmemDownEndDt]").attr('disabled', true);
		$("[name = nonmemDownEndDt]").attr('readonly', true);
		$("[name = nonmemDownEndDt]").val("");
		$("[name = nonmemDownAmt]").attr('readonly', true);
		$("[name = nonmemDownAmt]").val("");
	}
}


function couponFunction(){
	if($("[name = couponGb]:checked").val() == '1'){
		
		$("[name = couponNum]").attr('readonly', false);
		$("[name = couponNum]").attr('disabled', true);
		$.ajax({
			url:"/couponGenerate.html",
			type:"POST",
			success:function(result){
				$("[name = couponNum]").val(result);
			}
		});
		
		$("[name = nonmemDownGb]").attr('disabled' ,false);
		$("#c1").prop("checked", true);
		
		$("[name = nonmemDownStarDt]").attr('disabled', true);
		$("[name = nonmemDownStarDt]").attr('readonly', true);
		$("[name = nonmemDownEndDt]").attr('disabled', true);
		$("[name = nonmemDownEndDt]").attr('readonly', true);
		$("[name = nonmemDownAmt]").attr('readonly', false);
		
		if($("[name = distrGb]:checked").val() == '2'){
			$("[name = memDownGb]").attr('disabled',true);
			$("[name = memDownGb]").attr("checked", false);
			$("[name = memDownAmt]").val("");
			$("[name = memDownStartDt]").attr('disabled', true);
			$("[name = memDownStartDt]").attr('readonly', true);
			$("[name = memDownEndDt]").attr('disabled', true);
			$("[name = memDownEndDt]").attr('readonly', true);
		}
	}else if($("[name = couponGb]:checked").val() == '2'){

		$("[name = couponNum]").val("");
		$("[name = couponNum]").attr('readonly', true);
		$("[name = nonmemDownGb]").attr('checked', false);
		$("[name = nonmemDownGb]").attr('disabled' ,true);
		$("[name = nonmemDownStarDt]").attr('disabled', true);
		$("[name = nonmemDownStarDt]").attr('readonly', true);
		$("[name = nonmemDownStarDt]").val("");
		$("[name = nonmemDownEndDt]").attr('disabled', true);
		$("[name = nonmemDownEndDt]").attr('readonly', true);
		$("[name = nonmemDownEndDt]").val("");
		$("[name = nonmemDownAmt]").attr('readonly', true);
		$("[name = nonmemDownAmt]").val("");

		if($("[name = distrGb]:checked").val() == '2'){
			$("[name = memDownGb]").attr('disabled', false);
			$("[name = memDownAmt]").attr('readonly', false);
			$("[name = memDownStartDt]").attr('disabled', true);
			$("[name = memDownStartDt]").attr('readonly', true);
			$("[name = memDownEndDt]").attr('disabled', true);
			$("[name = memDownEndDt]").attr('readonly', true);
			$("#d1").prop('checked', true);
		}
	}
}


function initialize(){
/* 	$("[name = couponNum]").attr('disabled', true); */
	if("${contentVO.distrGb}" == "1"){
		$("#m1").prop("checked",true);
	}else if("${contentVO.distrGb}" == "2"){
		$("#m2").prop("checked", true);
	}
	
	if("${contentVO.memDownGb}" == "1"){
		$("#d1").prop("checked",true);
		$("#memDownStartDt").attr("readonly", true);
		$("#memDownEndDt").attr("readonly", true);
	}
	
	if("${contentVO.couponGb}"== "1"){
		$("[name = couponNum]").attr('disabled', true);
	}else{
		$("#nonmemDownGb").attr('disabled', false);
		$("#nonmemDownGb").attr('checked', false);
	}
	
	if("${contentVO.nonmemDownGb}" =="2"){
		$("#nonmemDownAmt").attr("readonly", true);
		$("#nonmemDownAmt").val("");
		var starDt =  "${contentVO.nonmemDownStarDt}".substring(0,10);
		var endDt =  "${contentVO.nonmemDownEndDt}".substring(0,10);
		$("#c_SDATE").val(starDt);
		$("#c_EDATE").val(endDt);
	}

	if("${contentVO.memDownGb}" == "3"&&!("${contentVO.distrGb}" =="2" && "${contentVO.couponGb}" == "1")){
		$("#d3").prop("checked", true);
		$("#memDownAmt").val("");
		$("#memDownAmt").attr("readonly", true);		
		$("#d_SDATE").attr("readonly", true);
		$("#d_EDATE").attr("readonly", true);
		$("#d_SDATE").attr("disabled", true);
		$("#d_EDATE").attr("disabled", true);
		$("#d_SDATE").val("");
		$("#d_EDATE").val("");
	}
	
	if("${contentVO.distrGb}" =="2" && "${contentVO.couponGb}" == "1"){
		$("#m2").prop("checked",true);
		$("#memDownAmt").attr("readonly", true);
		$("#memDownAmt").val("");
		$("#d_SDATE").attr("readonly", true);
		$("#d_EDATE").attr("readonly", true);
		$("[name=memDownGb]").attr("disabled", true);
		$("[name=memDownGb]").prop("checked", false);
		if("${contentVO.nonmemDownGb}" =="1"){
			$("#c_SDATE").attr("readonly", true);
			$("#c_EDATE").attr("readonly", true);
		}
	}

	if("${contentVO.memDownGb}" == "2"){
		$("#d2").prop("checked", true);
		$("#memDownAmt").val("");
		$("#memDownAmt").attr("readonly", true);
		var starDt =  "${contentVO.memDownStartDt}".substring(0,10);
		var endDt =  "${contentVO.memDownEndDt}".substring(0,10);

		$("#d_SDATE").val(starDt);
		$("#d_EDATE").val(endDt);
	} 

	if("${contentVO.couponGb}" == "2"){
		$("[name=nonmemDownGb]").attr("disabled", true);
		$("[name=nonmemDownGb]").prop("checked", false);
		$("#nonmemDownAmt").attr("readonly", true);
		$("#nonmemDownAmt").val("");
		$("#couponNum").val("");
		$("#couponNum").attr("disabled", true);
		$("#c_SDATE").attr("readonly", true);
		$("#c_EDATE").attr("readonly", true);
		$("#c_SDATE").attr("disabled", true);
		$("#c_EDATE").attr("disabled", true);
		$("#c_SDATE").val("");''
		$("#c_EDATE").val("");
	}

	if("${contentVO.memDownGb}"=="1"){
		$("#d_SDATE").attr("readonly", true);
		$("#d_EDATE").attr("readonly", true);
		$("#d_SDATE").attr("disabled", true);
		$("#d_EDATE").attr("disabled", true);
		$("#d_SDATE").val("");
		$("#d_EDATE").val("");
	}

	if("${contentVO.nonmemDownGb}"=="1"){
		$("#c_SDATE").attr("readonly", true);
		$("#c_EDATE").attr("readonly", true);
		$("#c_SDATE").attr("disabled", true);
		$("#c_EDATE").attr("disabled", true);
		$("#c_SDATE").val("");
		$("#c_EDATE").val("");
	}

	if("${contentVO.nonmemDownGb}"=="3"){
		$("#nonmemDownAmt").attr("readonly", true);
		$("#nonmemDownAmt").val("");
		
		$("#c_SDATE").attr("readonly", true);
		$("#c_EDATE").attr("readonly", true);
		$("#c_SDATE").attr("disabled", true);
		$("#c_EDATE").attr("disabled", true);
		$("#c_SDATE").val("");
		$("#c_EDATE").val("");
	}

	if("${contentVO.completGb}" == "2"){
		$("[name = distrGb]").attr("checked", false);
		$("[name = couponGb]").attr("checked", false);
	}

	if("<c:out value='${userRole}'/>" == "[ROLE_COMPANY_CREATOR]"){
		$("[name=useGb]").attr("disabled", true);
		$("[name=completGb]").attr("disabled", true);
		$("[name=distrGb]").attr("disabled", true);
		$("[name=memDownGb]").attr("disabled", true);
		$("[name=nonmemDownGb]").attr("disabled", true);
		$("[name=couponGb]").attr("disabled", true);
		$("[name=memDownAmt]").attr("readonly", true);
		$("[name=nonmemDownAmt]").attr("readonly", true);
		$("#d_SDATE").attr("readonly", true);
		$("#d_EDATE").attr("readonly", true);
		$("#c_SDATE").attr("readonly", true);
		$("#c_EDATE").attr("readonly", true);
	}
}
function cancelResist(){
	//message : 지금까지 입력한 자료가 사라집니다. 취소하시겠습니까?
	if(confirm("<spring:message code='contents.modify.041' />")){
		window.location.href="/contents/list.html?page=1";
	}
}
//콘텐츠 삭제 
function contentDelete(){
	if(confirm("<spring:message code='user.list.027' />")){
		$("#contents_modify_f").attr('action', '/contents/deleteContents.html');
		$("#contents_modify_f").submit();
		//document.appForm.action='/contents/deleteContents.html';
		//document.appForm.submit();
	}
}
</script>



</head>
<body>


<!-- wrap -->
<div id="wrap" class="sub_wrap">
	<!-- header -->
	<%@ include file="../inc/header.jsp" %>
	<!-- //header -->

	<!-- conteiner -->
	<div id="container">
		<div class="contents join_area">
			<!-- 콘텐츠 수정 -->
			<h2><spring:message code='contents.modify.001' /></h2>
			
			<form action="/contents/modify.html" name="contents_modify_f" id="contents_modify_f" method="POST" enctype="multipart/form-data">
			<div class="section fisrt_section">
				<div class="table_area">
					<table class="rowtable writetable">
						<colgroup>
							<col style="width:150px">
							<col style="">
							<col style="width:70px">
							<col style="width:150px">
							<!-- <col style="width:225px"> -->
						</colgroup>
						<tr>
							<th scope="row"><label class="title" for="contentsName"><em>*</em> <spring:message code='contents.modify.002' /></label></th>
							<td colspan="3">
								<input id="contentsName" name="contentsName" value="${contentVO.contentsName }" type="text" style="width:94.7%;">						
							</td>
						</tr>
						<tr>
							<th scope="row"><label class="title" for="contentsType"><em>*</em> <spring:message code='contents.modify.003' /></label></th>
							<td>
								<select id="contentsType" name="contentsType" style="width:150px;">
								<c:choose>
									<c:when test = "${contentVO.contentsType == '1' }">
										<option value="1" selected="selected">HTML5</option>
										<option value="2">ePub3</option>
										<option value="3">PDF</option>
									</c:when>
									<c:when test = "${contentVO.contentsType == '2' }">
										<option value="1">HTML5</option>
										<option value="2" selected="selected">ePub3</option>
										<option value="3">PDF</option>
									</c:when>
									<c:when test = "${contentVO.contentsType == '3' }">
										<option value="1">HTML5</option>
										<option value="2">ePub3</option>
										<option value="3" selected="selected">PDF</option>
									</c:when>
								</c:choose>
								</select>
							</td>
							<th style="text-align:right; width:150px;" scope="row"><label class="title" for="verNum"><em>*</em> <spring:message code='contents.modify.015' /></label></th>
							<td>
								<input id="verNum" name="verNum" type="text" value="${contentVO.verNum }" style="width:80%;">
								<%-- <input id="verNum" name="verNum" type="text" value="${contentVO.verNum }" style="width:50%;" class="line_right"> --%>
								<%-- <a href="#" class="btn btnL btn_gray_light line_left"><spring:message code='contents.modify.004' /></a>		 --%>			
							</td>
						</tr>
						<tr>
							<th scope="row"><label class="title" for="appName"><spring:message code='contents.modify.005' /></label></th>
							<td colspan="3">
								<input id="relatedAppName" name="relatedAppName" type="text" value="${contentVO.appName }" style="width:79%;" class="line_right">
								<input id="relatedBundleId" name="relatedBundleId" type="hidden" value="${contentVO.contentsappSubVO.storeBundleId }" style="width:79%;" class="line_right">
								<input id="relatedAppType" name="relatedAppType" type="hidden" value="${contentVO.appType }" style="width:79%;" class="line_right">
								<input id="relatedInAppSeq" name="relatedInAppSeq" type="hidden" value="${contentVO.contentsappSubVO.inappSeq }"/>
								<a href="#" id="appSearch" class="btn btnL btn_gray_light line_left"><spring:message code='contents.modify.006' /></a>
							</td>
						</tr>
						<tr>
							<th scope="row"><label class="title" for="descriptionText"><spring:message code='contents.modify.007' /></label></th>
							<td colspan="3">
								<textarea id="descriptionText" name="descriptionText" cols="" rows="4" style="width:95%;">${contentVO.descriptionText}</textarea>
							</td>
						</tr>
						<tr>
							<th scope="row"><label class="title" for="contentsFile"><em>*</em> <spring:message code='contents.modify.008' /></label></th>
							<td colspan="3">
								<input id="contentsFile" name="contentsFile" type="file" style="width:95%;">
								<c:choose>
									<c:when test='${not empty contentVO.uploadOrgFile}'>
										<div id="uploadInput">
											<span id="uploadOrgName" >${contentVO.uploadOrgFile}</span>										<!--message : 아이콘 썸네일 이미지 닫기  -->
											<a class="removeImgBtn2" id="removeImgBtn2" href="#removeImgBtn2"><img src="/images/btn_close_s.png" alt="<spring:message code='contents.modify.042' />"></a>
										</div>
									</c:when>
									<c:when test="${empty contentVO.uploadOrgFile}">
										<div id="uploadInput">
										</div>
									</c:when>
								</c:choose>
							</td>
						</tr>
						<tr>
							<th scope="row"><span class="title"><em>*</em> <spring:message code='contents.modify.012' /></span></th>
							<td colspan="3">
								<div class="radio_area">
									<c:choose>
										<c:when test="${contentVO.useGb == '1'}">
											<input name="useGb" id="u_y"  type="radio" value="1" checked="checked"> <label for="u_y"><spring:message code='contents.modify.013' /></label>
											<input name="useGb" id="u_n"  type="radio" value="2"> <label for="u_n"><spring:message code='contents.modify.014' /></label>
										</c:when>
										<c:when test="${contentVO.useGb == '2'}">
											<input name="useGb" id="u_y"  type="radio" value="1"> <label for="u_y"><spring:message code='contents.modify.013' /></label>
											<input name="useGb" id="u_n"  type="radio" value="2" checked="checked"> <label for="u_n"><spring:message code='contents.modify.014' /></label>
										</c:when>
									</c:choose>
								</div>
							</td>
						</tr>
						<tr>
							<th scope="row"><span class="title"><em>*</em> <spring:message code='contents.modify.016' /></span></th>
							<td colspan="3">
								<div class="radio_area">
									<c:choose>
										<c:when test="${contentVO.completGb == '1'}">
											<input name="completGb" id="c_y"  type="radio" value="1" checked="checked"> <label for="c_y"><spring:message code='contents.modify.013' /></label>
											<input name="completGb" id="c_n"  type="radio" value="2"> <label for="c_n"><spring:message code='contents.modify.014' /></label>
										</c:when>
										<c:when test="${contentVO.completGb == '2'}">
											<input name="completGb" id="c_y"  type="radio" value="1"> <label for="c_y"><spring:message code='contents.modify.013' /></label>
											<input name="completGb" id="c_n"  type="radio" value="2" checked="checked"> <label for="c_n"><spring:message code='contents.modify.014' /></label>
										</c:when>
									</c:choose>
								</div>
							</td>
						</tr>
						<tr>
							<th scope="row"><span class="title"><em>*</em> <spring:message code='contents.modify.019' /></span></th>
							<td colspan="3">
								<div class="radio_area coupon_area" style="width:100%">
									<span style="width:100%; margin-top:9px;">													
										<input type="radio" name="distrGb" id="m1"  value="1" /> <label for="m1"><spring:message code='contents.modify.020' /></label><!--message : 회원 로그인  -->
										<input type="radio" name="distrGb" id="m2"  value="2" /> <label for="m2"><spring:message code='contents.modify.021' /></label><br/><!--message : 비회원  -->
									</span>
									<span>
										<input type="radio" name="memDownGb" id="d1"  value="1" /> <label for="d1"><spring:message code='contents.modify.025' /></label><!--message : 다운로드 횟수  -->
										<input name="memDownAmt" id="memDownAmt" type="text" value="${contentVO.memDownAmt}" class="tCenter" style="width:120px;">
										&nbsp;&nbsp; <spring:message code='anonymous.option.010' />&nbsp;<input name ="memDownCnt" value="${contentVO.memDownCnt }" type="text" class="tCenter" style="width:120px;" readonly onkeypress="return digit_check(event)" maxlength="3"/>
									</span>
									<br>
									<span>
										<input type="radio" name="memDownGb" id="d2" id=""  value="2" /> <label for="d2"><spring:message code='contents.modify.026' /></label><!--message : 유효기간  -->
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="d_SDATE" name="memDownStartDt" type="text" title="" value="${memDownStartDt }" class="date fmDate1" value="" />
										&nbsp;&nbsp;~&nbsp;&nbsp;
										<input id="d_EDATE" name="memDownEndDt" type="text" title="" value="${contentVO.memDownEndDt }"  class="date toDate1" value="" />
									</span>
									<br>
									<span>
										<input type="radio" name="memDownGb" id="d3"  value="3" /> <label for="d3"><spring:message code='contents.modify.032' /></label><!--message : 취소  -->
									</span>
								</div>
							</td>
						</tr>
						<tr>
							<th scope="row"><span class="title spacing"><em>*</em> <spring:message code='contents.modify.022' /></span> <br>&nbsp;&nbsp;&nbsp;<!-- <a href="#"><img src="../images/btn_q.jpg" alt="" title="쿠폰 사용 도움말"></a>--></th>
							<td colspan="3">
								<div class="radio_area coupon_area">
									<span style="width:100%;">
										<c:choose>
											<c:when test="${contentVO.couponGb == 1}">
												<input name="couponGb" id="cou_y"  type="radio" value="1" checked="checked"> <label for="cou_y"><spring:message code='contents.modify.023' /></label>
												<input name="couponGb" id="cou_n"  type="radio" value="2"> <label for="cou_n" style="margin-right:43px"><spring:message code='contents.modify.024' /></label>
											</c:when>
											<c:when test="${contentVO.couponGb == 2}">
												<input name="couponGb" id="cou_y"  type="radio" value="1"> <label for="cou_y"><spring:message code='contents.modify.023' /></label>
												<input name="couponGb" id="cou_n"  type="radio" value="2" checked="checked"> <label for="cou_n" style="margin-right:43px"><spring:message code='contents.modify.024' /></label>
											</c:when>
											<c:otherwise>
												<input name="couponGb" id="cou_y"  type="radio" value="1"> <label for="cou_y"><spring:message code='contents.modify.023' /></label>
												<input name="couponGb" id="cou_n"  type="radio" value="2"> <label for="cou_n" style="margin-right:43px"><spring:message code='contents.modify.024' /></label>
											</c:otherwise>
										</c:choose>
										<input id="couponNum" name="couponNum" value="${contentVO.couponNum }" type="text"  style="width:43.2%;">
									</span>
									<br/>
									<span style="width:100%;">
										<c:choose>
											<c:when test="${contentVO.nonmemDownGb == '1' }">
												<input type="radio" name="nonmemDownGb" id="c1"  value="1" checked="checked" /> <label for="c1"><spring:message code='contents.modify.025' /></label>
											</c:when>
											<c:when test="${contentVO.nonmemDownGb == '2' }">
												<input type="radio" name="nonmemDownGb" id="c1"  value="1" /> <label for="c1"><spring:message code='contents.modify.025' /></label>
											</c:when>
											<c:when test="${contentVO.nonmemDownGb == '3' }">
												<input type="radio" name="nonmemDownGb" id="c1"  value="1" /> <label for="c1"><spring:message code='contents.modify.025' /></label>
											</c:when>
											<c:otherwise>
												<input type="radio" name="nonmemDownGb" id="c1"  value="1" /> <label for="c1"><spring:message code='contents.modify.025' /></label>
											</c:otherwise>
										</c:choose>
										
										<input name="nonmemDownAmt" id="nonmemDownAmt" type="text" value="${contentVO.nonmemDownAmt }" class="tCenter" style="width:120px; margin-right:3px;">
										&nbsp;&nbsp; <spring:message code='anonymous.option.010' />&nbsp;<input name="nonmemDownCnt" value='${contentVO.nonmemDownCnt }' type="text"  class="tCenter" style="width:120px; " readonly value="" onkeypress="return digit_check(event)" maxlength="3"/>
									</span>
										<br>
										<span style ="width:100%;">
											<c:choose>
												<c:when test="${contentVO.nonmemDownGb == '1' }">
													<input type="radio" name="nonmemDownGb" id="c2"  value="2" /> <label for="c2"><spring:message code='contents.modify.026' /></label>
												</c:when>
												<c:when test="${contentVO.nonmemDownGb == '3' }">
													<input type="radio" name="nonmemDownGb" id="c2"  value="2" /> <label for="c2"><spring:message code='contents.modify.026' /></label>
												</c:when>
												<c:when test="${contentVO.nonmemDownGb == '2' }">
													<input type="radio" name="nonmemDownGb" id="c2"  value="2" checked="checked" /> <label for="c2"><spring:message code='contents.modify.026' /></label>
												</c:when>
												<c:otherwise>
													<input type="radio" name="nonmemDownGb" id="c2"  value="2" /> <label for="c2"><spring:message code='contents.modify.026' /></label>
												</c:otherwise>
											</c:choose>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="c_SDATE" name="nonmemDownStarDt" type="text" title="" value="${nonmemDownStarDt }" class="date fmDate2 startDate" value="" />
											&nbsp;&nbsp;~&nbsp;&nbsp;
											<input id="c_EDATE" name="nonmemDownEndDt" type="text" title=""  value="${nonmemDownEndDt  }"  class="date toDate2 endDate" value="" />
										</span>
										<br>
										<span style="width:100%;">
											<c:choose>
												<c:when test="${contentVO.nonmemDownGb == '3' }">
													<input type="radio" name="nonmemDownGb" id="c3"  value="3" checked="checked" /> <label for="c3"><spring:message code='contents.modify.032' /></label>
												</c:when>
												<c:otherwise>
													<input type="radio" name="nonmemDownGb" id="c3"  value="3" /> <label for="c3"><spring:message code='contents.modify.032' /></label>
												</c:otherwise>
											</c:choose>
										</span>
								</div>
							</td>
						</tr>
						<tr>
							<sec:authorize access="hasRole('ROLE_ADMIN_SERVICE')">
								<th scope="row"><span class="title"><em>*</em> <spring:message code='contents.modify.027' /></span></th>
								<td colspan="3">
									<div class="radio_area">
										<c:choose>
											<c:when test="${contentVO.limitGb == '1' }">
												<input name="limitGb" id="l_y"  type="radio" value="1" checked="checked"> <label for="l_y"><spring:message code='contents.modify.023' /></label>	
												<input name="limitGb" id="l_n"  type="radio" value="2"> <label for="l_n"><spring:message code='contents.modify.024' /></label>
											</c:when>
											<c:when test="${contentVO.limitGb == '2' }">
												<input name="limitGb" id="l_y"  type="radio" value="1"> <label for="l_y"><spring:message code='contents.modify.023' /></label>	
												<input name="limitGb" id="l_n"  type="radio" value="2" checked="checked"> <label for="l_n"><spring:message code='contents.modify.024' /></label>
											</c:when>
										</c:choose>
									</div>
								</td>
							</sec:authorize>
						</tr>
					</table>
				</div>

				<div class="btn_area_bottom tCenter">
					<a href="#" id="modifyBtn" class="btn btnL btn_red"><spring:message code='contents.modify.030' /></a>
					<a href="javascript:cancelResist();" class="btn btnL btn_gray_light"><spring:message code='contents.modify.031' /></a>
					<c:choose>
						<c:when test="${'2' eq contentVO.completGb }">
							<a href="javascript:contentDelete();" class="btn btnL btn_gray_light"><spring:message code='user.list.011'/></a>		
						</c:when>
						<c:otherwise>
							<sec:authorize access="hasRole('ROLE_COMPANY_DISTRIBUTOR') || hasRole('ROLE_ADMIN_SERVICE') || hasRole('ROLE_COMPANY_MIDDLEADMIN')">
								<a href="javascript:contentDelete();" class="btn btnL btn_gray_light"><spring:message code='user.list.011'/></a>
							</sec:authorize>	
						</c:otherwise>
					</c:choose>
					<input type="hidden" name="contentsSeq" 		value="${param.contentsSeq }"/>
					<input type="hidden" name="contentsappSubSeq"   value="${contentVO.contentsappSubVO.contentsappSubSeq }">
					<input type="hidden" name="page" value="${param.page }"/>
					<input type="hidden" name="uploadSaveFile" value="${contentVO.uploadSaveFile}"/>
					<input type="hidden" name="fileChanged" id="fileChanged" value="0"/><%-- 0 is no change--%>
					<input type="hidden" name="companySeq" value="<sec:authentication property="principal.memberVO.companySeq" />"/>
					<input type="hidden" name="chgUserSeq" value="<sec:authentication property="principal.memberVO.userSeq" />"/>
					<input type="hidden" name="chgUserId" value="<sec:authentication property="principal.memberVO.userId" />"/>
					<input type="hidden" name="chgUserGb" value="<sec:authentication property="principal.memberVO.userGb" />"/>
				</div> 
			</div>
			</form>
			<!-- //콘텐츠 수정 -->
		</div>
	</div>
	<!-- //conteiner -->
	<!-- footer -->
	<%@ include file="../inc/footer.jsp" %>
	<!-- //footer -->

</div><!-- //wrap -->

</body>
</html>