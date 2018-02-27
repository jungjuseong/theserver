<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fm" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="curi" value="${requestScope['javax.servlet.forward.request_uri']}" />
	<div class="tab_area">
		<ul>
			<sec:authorize access="hasAnyRole('ROLE_COMPANY_MIDDLEADMIN','ROLE_COMPANY_MEMBER')">
			<!--messsage : 사용자  -->
				<li <c:if test="${fn:containsIgnoreCase(curi, '/user/')}">class="current last"</c:if>><a href="/man/user/list.html?page=1&searchType=&searchValue=&isAvailable=false"><spring:message code='man.header.001' /></a></li>
			</sec:authorize>
			<sec:authorize access="hasRole('ROLE_ADMIN_SERVICE')">
			<!--message : 회원  -->
				<li <c:if test="${fn:containsIgnoreCase(curi, '/user/')}">class="current last"</c:if>><a href="/man/user/list.html?page=1&searchType=&searchValue=&isAvailable=false"><spring:message code='man.header.002' /></a></li>
			</sec:authorize>
			<sec:authorize access="hasAnyRole('ROLE_COMPANY_MIDDLEADMIN','ROLE_COMPANY_MEMBER')">
				<li <c:if test="${fn:containsIgnoreCase(curi, '/department/')}">class="current last"</c:if>><a href="/man/department/management.html"> <spring:message code='extend.local.073' /></a></li>
				<li <c:if test="${fn:containsIgnoreCase(curi, '/notice/')}">class="current last"</c:if>><a href="/man/notice/list.html?page=1"> <spring:message code='extend.local.072' /></a></li>
				
			<!--message : 앱 카테고리  -->
				<%-- <li <c:if test="${fn:containsIgnoreCase(curi, '/category/')}">class="current last"</c:if>><a href="#"><spring:message code='man.header.003' /></a></li> --%>
			<!--message : Distribution Profile  -->
				<li <c:if test="${fn:containsIgnoreCase(curi, '/provision/')}">class="current last"</c:if>><a href="/man/provision/list.html"><spring:message code='man.header.004' /></a></li>
				<li <c:if test="${fn:containsIgnoreCase(curi, '/device/')}">class="current last"</c:if>><a href="/man/device/list.html?page=1"><spring:message code='extend.local.043' /></a></li>
				</sec:authorize>
			<sec:authorize access="hasRole('ROLE_ADMIN_SERVICE')">
				<li <c:if test="${fn:containsIgnoreCase(curi, '/preference/')}">class="current last"</c:if>><a href="/man/preference/modify.html"><spring:message code='extend.local.011' /></a></li>
			</sec:authorize>
		</ul>
	</div>