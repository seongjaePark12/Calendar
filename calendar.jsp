<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>calendar.jsp(달력연습)</title>
	<%@ include file="/WEB-INF/views/include/bs4.jsp" %>
  <style>
    #td1,#td8,#td15,#td22,#td29,#td36 {
      color: red;
    }
    #td7,#td14,#td21,#td28,#td35,#td42 {
      color: blue;
    }
    .today {
      font-size: 1.5em;
      background-color: pink;
      color: #fff;
      /* text-align: center; */
    }
  </style>
</head>
<body>
<%@ include file="/WEB-INF/views/include/nav.jsp" %>
<%@ include file="/WEB-INF/views/include/slide2.jsp" %>
<p><br/></p>
<div class="container">
  <div class="col-sm-12" style="margin:30px 0px;font-size:1.4em;text-align:center">
    <button class="btn btn-secondary btn-sm" onclick="location.href='${ctp}/study/calendar?yy=${yy-1}&mm=${mm}'" title="이전년도">◁◁</button>
    <button class="btn btn-secondary btn-sm" onclick="location.href='${ctp}/study/calendar?yy=${yy}&mm=${mm-1}'" title="이전월">◀</button>
    &nbsp; <font size="5">${yy}년 ${mm+1}</font> &nbsp;
    <button class="btn btn-secondary btn-sm" onclick="location.href='${ctp}/study/calendar?yy=${yy}&mm=${mm+1}'" title="다음월">▶</button>
    <button class="btn btn-secondary btn-sm" onclick="location.href='${ctp}/study/calendar?yy=${yy+1}&mm=${mm}'" title="다음년도">▷▷</button>
    &nbsp; &nbsp;
    <button class="btn btn-secondary btn-sm" onclick="location.href='${ctp}/study/calendar'" title="오늘날짜"><i class="fa fa-home"></i></button>
  </div>
  <br/>
  <div class="row">
    <div class="col-sm-12" style="height:450px;">
      <table class="table table-bordered" style="height:100%">
        <tr style="text-align:center;font-size:1em;background-color:#eee;height:10%;">
          <th style="color:red;width:13%;vertical-align:middle;">일</th>
          <th style="width:13%;vertical-align:middle;">월</th>
          <th style="width:13%;vertical-align:middle;">화</th>
          <th style="width:13%;vertical-align:middle;">수</th>
          <th style="width:13%;vertical-align:middle;">목</th>
          <th style="width:13%;vertical-align:middle;">금</th>
          <th style="color:blue;width:13%;vertical-align:middle;">토</th>
        </tr>
        <tr>
          <c:set var="cnt" value="1"/>
          <!-- 이전월의 마지막 날짜를 이용하여 앞쪽의 빈 공백을 채워준다.(요일의 숫자는 일요일은 '1', 토요일은 '7'이다. -->
          <!-- 즉, 2022년 1월을 예로들면 12월은 마지막날짜(preLastDay)가 31이고, 1월의 첫번째요일은 토요일(7)이기에 출력할 이전월(12월)의 시작일은 '31-(7-2)=26'일부터 출력해야한다.(-2는 상수값이다) -->
          <c:forEach var="preDay" begin="${preLastDay - (startWeek - 2) }" end="${preLastDay}">	<!-- 이전월(12월)의 26일부터 마지막일자(preLastDay : 31일)까지 출력한다. -->
            <td id="td${cnt}" style="color: #ddd;font-size:0.6em">
              ${preYear}-${preMonth+1}-${preDay}
            </td>
            <c:set var="cnt" value="${cnt=cnt+1}"/>
          </c:forEach>
          
          <!-- 해당월에 대한 날짜를 반복 출력한다. -->
          <c:forEach begin="1" end="${lastDay}" varStatus="st">
            <c:set var="todaySw" value="${yy == toYear && mm == toMonth && st.count == toDay ? 1 : 0}"/>	<!-- 오늘날짜가 맞으면 toDay변수에 1을 셋팅 -->
            <td id="td${cnt}" ${todaySw == 1 ? 'class=today' : '' } style="font-size:0.9em;padding:8px;">	<!-- 오늘날짜가 맞으면 지정한 class로 디자인처리한다. -->
              ${st.count}
            </td>
            <c:if test="${cnt % 7 == 0}">		<!-- 한행이 꽉찾으면 다음줄로 넘어간다. -->
              </tr><tr>
            </c:if>
            <c:set var="cnt" value="${cnt=cnt+1}"/>	<!-- 일수를 한개 증가 누적한다. -->
          </c:forEach>
          
          <!-- 다음월의 날짜를 출력한다.(출력시작위치는 다음달 1일에 해당하는 요일(nextStartWeek)부터이다. -->
          <c:forEach begin="${nextStartWeek}" end="7" varStatus="nextDay">
            <td id="td${cnt}" style="color: #ddd;font-size:0.6em">
              ${nextYear}-${nextMonth+1}-${nextDay.count}
            </td>
            <c:set var="cnt" value="${cnt=cnt+1}"/>
          </c:forEach>
        </tr>
      </table>
    </div>
  </div>
</div>
<p><br/></p>
<%@ include file="/WEB-INF/views/include/footer.jsp" %>
</body>
</html>