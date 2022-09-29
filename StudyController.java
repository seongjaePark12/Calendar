package com.spring.javagreenS;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.javagreenS.common.ARIAUtil;
import com.spring.javagreenS.service.MemberService;
import com.spring.javagreenS.service.StudyService;
import com.spring.javagreenS.vo.ChartVO;
import com.spring.javagreenS.vo.KakaoAddressVO;
import com.spring.javagreenS.vo.KakaoAreaVO;
import com.spring.javagreenS.vo.MailVO;
import com.spring.javagreenS.vo.OperatorVO;
import com.spring.javagreenS.vo.PersonVO;

@Controller
@RequestMapping("/study")
public class StudyController {
	
	@Autowired
	StudyService studyService;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	JavaMailSender mailSender;
	
	@Autowired
	MemberService memberService;
	
	@RequestMapping(value = "/password/passCheck1", method = RequestMethod.GET)
	public String passCheck1Get() {
		return "study/password/passCheck1";
	}
	
	@RequestMapping(value = "/password/passCheck1", method = RequestMethod.POST)
	public String passCheck1Post(long pwd, Model model) {
		// 암호화를 위한 키 : 0x1234ABCD
		long key = 0x1234ABCD;
		long encPwd, decPwd;
		
		encPwd = pwd ^ key;    // 암호화 : DB에 저장시켜준다.
		
		decPwd = encPwd ^ key;    // 복호화
		
		model.addAttribute("pwd", pwd);
		model.addAttribute("encPwd", encPwd);
		model.addAttribute("decPwd", decPwd);
		
		return "study/password/passCheck1";
	}
	
	@RequestMapping(value = "/password/passCheck2", method = RequestMethod.POST)
	public String passCheck2Post(String pwd, Model model) {
		// 입력문자가 영문 소문자일경우는 대문자로 변경처리(연산시에 자리수 Over 때문에...)
		pwd = pwd.toUpperCase();
		
		// 입력된 비밀번호를 아스키코드로 변환하여 누적처리
		long intPwd;
		String strPwd = "";
		for(int i=0; i<pwd.length(); i++) {
			intPwd = (long) pwd.charAt(i);
			strPwd += intPwd;
		}
		// 문자로 결합된 숫자를, 연산하기위해 다시 숫자로 변환한다.
		intPwd = Long.parseLong(strPwd);
		
		// 암호화를 위한 키 : 0x1234ABCD
		long key = 0x1234ABCD;
		long encPwd, decPwd;
		
		// 암호화를 위한 EOR 연산하기
		encPwd = intPwd ^ key;
		strPwd = String.valueOf(encPwd);  // 암호화 : DB에 저장시켜준다.
		model.addAttribute("encPwd", strPwd);	// 암호화된 문자...
		
		// 복호화 작업처리
		intPwd = Long.parseLong(strPwd);
		decPwd = intPwd ^ key;
		strPwd = String.valueOf(decPwd);
		
		// 복호화된 문자형식의 아스키코드값을 2개씩 분류하여 실제문자로 변환해준다.
		String result = "";
		char ch;
		
		for(int i=0; i<strPwd.length(); i+=2) {
			ch = (char) Integer.parseInt(strPwd.substring(i, i+2));
			result += ch;
		}
		model.addAttribute("decPwd", result);
		model.addAttribute("pwd", pwd);
		return "study/password/passCheck1";
	}
	
	@RequestMapping(value = "/password2/operatorMenu", method = RequestMethod.GET)
	public String operatorMenuGet() {
		return "study/password2/operatorMenu";
	}
	
	@RequestMapping(value = "/password2/operatorInputOk", method = RequestMethod.POST)
	public String operatorInputOkPost(OperatorVO vo) {
		// 아이디 중복체크
		OperatorVO vo2 = studyService.getOperator(vo.getOid());
		if(vo2 != null) return "redirect:/msg/operatorCheckNo";
		
		// 아이디가 중복되지 않았으면 비밀번호를 암호화 하여 저장시켜준다. service객체에서 처리한다.
		studyService.setOperatorInputOk(vo);
		
		return "redirect:/msg/operatorInputOk";
	}
	
	@RequestMapping(value = "/password2/operatorList", method = RequestMethod.GET)
	public String operatorListGet(Model model) {
		ArrayList<OperatorVO> vos = studyService.getOperatorList();
		
		model.addAttribute("vos", vos);
		
		return "study/password2/operatorList";
	}
	
	@ResponseBody
	@RequestMapping(value = "/password2/operatorDelete", method = RequestMethod.POST)
	public String operatorDeletePost(String oid) {
		studyService.setOperatorDelete(oid);
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = "/password2/operatorSearch", method = RequestMethod.POST)
	public String operatorSearchPost(OperatorVO vo) {
		String res = studyService.setOperatorSearch(vo);
		return res;
	}
	
	
	
	
	@RequestMapping(value = "/ajax/ajaxMenu", method = RequestMethod.GET)
	public String ajaxMenuGet() {
		return "study/ajax/ajaxMenu";
	}
	
	@RequestMapping(value = "/ajax/ajaxTest1", method = RequestMethod.GET)
	public String ajaxTest1Get() {
		return "study/ajax/ajaxTest1";
	}
	
	// 배열을 이용한 값의 전달
	@ResponseBody
	@RequestMapping(value = "/ajax/ajaxTest1", method = RequestMethod.POST)
	public String[] ajaxTest1Post(String dodo) {
//		String[] strArr = new String[100];
//		strArr = studyService.getCityStringArr(dodo);
//		return strArr;
		return studyService.getCityStringArr(dodo);
	}
	
	@RequestMapping(value = "/ajax/ajaxTest2", method = RequestMethod.GET)
	public String ajaxTest2Get() {
		return "study/ajax/ajaxTest2";
	}
	
	// ArrayList의 String배열을 이용한 값의 전달
	@ResponseBody
	@RequestMapping(value = "/ajax/ajaxTest2", method = RequestMethod.POST)
	public ArrayList<String> ajaxTest2Post(String dodo) {
		return studyService.getCityArrayListStr(dodo);
	}
	
	
	@RequestMapping(value = "/ajax/ajaxTest3", method = RequestMethod.GET)
	public String ajaxTest3Get() {
		return "study/ajax/ajaxTest3";
	}
	
	// HashMap을 이용한 값의 전달(배열, ArrayList에 담아온 값을 다시 map에 담아서 넘길 수 있다.)
	@ResponseBody
	@RequestMapping(value = "/ajax/ajaxTest3", method = RequestMethod.POST)
	public HashMap<Object, Object> ajaxTest3Post(String dodo) {
		ArrayList<String> vos = new ArrayList<String>();
		vos = studyService.getCityArrayListStr(dodo);
		
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		map.put("city", vos);
		
		return map;
	}
	
	@RequestMapping(value = "/ajax/ajaxTest4", method = RequestMethod.GET)
	public String ajaxTest4Get() {
		return "study/ajax/ajaxTest4";
	}
	
	// vo객체를 이용한 값의 전달
	@ResponseBody
	@RequestMapping(value = "/ajax/ajaxTest4", method = RequestMethod.POST)
	public OperatorVO ajaxTest4Post(String oid) {
		return studyService.getOperator(oid);
	}
	
	// vos객체를 이용한 값의 전달(ArrayList)
	@ResponseBody
	@RequestMapping(value = "/ajax/ajaxTest5", method = RequestMethod.POST)
	public ArrayList<OperatorVO> ajaxTest5Post(String oid) {
//		ArrayList<OperatorVO> vos = new ArrayList<OperatorVO>();
//		vos = studyService.getOperatorVos(oid);
//		return vos;z
		return studyService.getOperatorVos(oid);
	}
	
	// aria 암호화 방식연습
	@RequestMapping(value = "/password3/aria", method = RequestMethod.GET)
	public String ariaGet() {
		return "study/password3/aria";
	}
	
	@ResponseBody
	@RequestMapping(value = "/password3/aria", method = RequestMethod.POST)
	public String ariaPost(String pwd) {
		String encPwd = "";
		String decPwd = "";
		
		try {
			encPwd = ARIAUtil.ariaEncrypt(pwd);
			decPwd = ARIAUtil.ariaDecrypt(encPwd);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		pwd = "Encoding : " + encPwd + " / Decoding : " + decPwd;
		
		return pwd;
	}
  
	
  // BCryptPasswordEncoder 암호화 방식연습
	@RequestMapping(value = "/password3/securityCheck", method = RequestMethod.GET)
	public String securityCheckGet() {
		return "study/password3/security";
	}
	
	@ResponseBody
	@RequestMapping(value = "/password3/securityCheck", method = RequestMethod.POST)
	public String securityCheckPost(String pwd) {
		String encPwd = "";
		
		encPwd = passwordEncoder.encode(pwd);
		
		pwd = "Encoding : " + encPwd + " / Source Password : " + pwd;
		
		return pwd;
	}
	
	// 메일폼 호출
	@RequestMapping(value = "/mail/mailForm", method = RequestMethod.GET)
	public String mailFormGet() {
		return "study/mail/mailForm";
	}
	
	// 메일전송
	@RequestMapping(value = "/mail/mailForm", method = RequestMethod.POST)
	public String mailFormPost(MailVO vo) {
		try {
			String toMail = vo.getToMail();
			String title = vo.getTitle();
			String content = vo.getContent();
			// 메세지를 변환시켜서 보관함(messageHelper)에 저장하여 준비한다.
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			// 메일보관함에 회원이 보낸온 메세지를 모두 저장시켜둔다.
			messageHelper.setTo(toMail);
			messageHelper.setSubject(title);
			messageHelper.setText(content);
			// 메세지 보관함의 내용을 편집해서 다시 보관함에 담아둔다.
			content = content.replace("\n", "<br>");
			content += "<br><hr><h3>길동이가 보냅니다.</h3><hr><br>";
			content += "<p><img src=\"cid:main.jpg\" width='500px'></p><hr>";
			content += "<p>방문하기 : <a href='http://49.142.157.251:9090/cjgreen'>javagreenJ사이트</a></p>";
			content += "<hr>";
			messageHelper.setText(content, true);
			// 본문에 기재된 그림파일의 경로를 따로 표시시켜준다.
			FileSystemResource file = new FileSystemResource("C:\\Users\\박성재\\git\\repository6\\javagreenS\\src\\main\\webapp\\resources\\images\\main.png");
			messageHelper.addInline("main.jpg", file);
			// 메일 전송하기
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return "redirect:/msg/mailSendOk";
	}
	
	// UUID 입력폼
	@RequestMapping(value = "/uuid/uuidForm", method = RequestMethod.GET)
	public String uuidFormGet() {
		return "study/uuid/uuidFrom";
	}
	
	// UUID 처리하기
	@ResponseBody
	@RequestMapping(value = "/uuid/uuidProcess", method = RequestMethod.POST)
	public String uuidProcessPost() {
		UUID uid = UUID.randomUUID();
		return uid.toString();
	}
	
	// 파일 업로드폼
	@RequestMapping(value = "/fileUpload/fileUpload", method = RequestMethod.GET)
	public String fileUploadGet() {
		return "study/fileUpload/fileUpload";
	}
	
	// 파일 업로드 처리하기
	@RequestMapping(value = "/fileUpload/fileUpload", method = RequestMethod.POST)
	public String fileUploadPost(MultipartFile fName) {
		int res = studyService.fileUpload(fName);
		if(res == 1) {
			return "redirect:/msg/fileUploadOk";
		}
		else {
			return "redirect:/msg/fileUploadNo";
		}
	}
	
	// 트랜잭션 연습을위한 person폼 불러오기
	@RequestMapping(value = "/personInput", method = RequestMethod.GET)
	public String personInputGet() {
		return "study/transaction/personInput";
	}
	
	// 트랜잭션 정보 저장하기
	@RequestMapping(value = "/personInput", method = RequestMethod.POST)
	public String personInputPost(PersonVO vo) {
		studyService.setPersonInput(vo);
		
		return "redirect:/msg/personInputOk";
	}
	
	// 트랜잭션 연습을위한 리스트 person 불러오기
	@RequestMapping(value = "/personList", method = RequestMethod.GET)
	public String personListGet(Model model) {
		ArrayList<PersonVO> vos = studyService.getPersonList();
		model.addAttribute("vos", vos);
		
		return "study/transaction/personList";
	}
	
  // 달력내역 가져오기
	@RequestMapping(value="/calendar", method=RequestMethod.GET)
	public String calendarGet() {
		studyService.getCalendar();
		return "study/calendar/calendar";
	}
	
	// 카카오맵 사용하기
	@RequestMapping(value="/kakaomap", method=RequestMethod.GET)
	public String kakaomapGet() {
		return "study/kakaomap/kakaomap";
	}
	
	// 카카오맵 응용하기1
	@RequestMapping(value="/kakaoEx1", method=RequestMethod.GET)
	public String kakaoEx1Get() {
		return "study/kakaomap/kakaoEx1";
	}
	
	// 카카오맵 응용하기1-2
	@ResponseBody
	@RequestMapping(value="/kakaoEx1", method=RequestMethod.POST)
	public String kakaoEx1Post(KakaoAddressVO vo) {
		KakaoAddressVO searchVo= studyService.getAddressName(vo.getAddress());
		if(searchVo != null) return "0";
		studyService.setAddressName(vo);
		
		return "1";
	}
	
	// 카카오맵 응용하기2
	@RequestMapping(value="/kakaoEx2", method=RequestMethod.GET)
	public String kakaoEx2Get(Model model,
			@RequestParam(name="address", defaultValue = "사창사거리", required = false)  String address) {
		List<KakaoAddressVO> vos = studyService.getAddressNameList();
		KakaoAddressVO vo = studyService.getAddressName(address);
		
		model.addAttribute("vos", vos);
		model.addAttribute("vo", vo);
		model.addAttribute("address", address);
		
		return "study/kakaomap/kakaoEx2";
	}
	
	// 카카오맵 응용2-2(지점명 DB에서 삭제하기)
	@ResponseBody
	@RequestMapping(value = "/kakaoEx2Delete", method = RequestMethod.POST)
	public String kakaoEx2DeleteGet(String address) {
		studyService.kakaoEx2Delete(address);
		return "";
	}
	
	// 카카오맵 응용하기3
	@RequestMapping(value="/kakaoEx3", method=RequestMethod.GET)
	public String kakaoEx3Get(Model model, String address) {
		if(address == null) address = "청주 사창사거리";
		model.addAttribute("address", address);
		return "study/kakaomap/kakaoEx3";
	}
	
	// 카카오맵 응용하기4
	@RequestMapping(value="/kakaoEx4", method=RequestMethod.GET)
	public String kakaoEx4Get(Model model) {
		String[] address1s = studyService.getAddress1();
		
		double latitude = 36.63508797975421;
		double longitude = 127.45959376343134;
  	
		model.addAttribute("address1s", address1s);
		
		model.addAttribute("latitude", latitude);
		model.addAttribute("longitude", longitude);
		
		return "study/kakaomap/kakaoEx4";
	}
	
	// '도시'를 선택해서 넘기면 각 '지역'을 검색해서 돌려준다. : ajax를 사용하여 처리함
	@ResponseBody
	@RequestMapping(value="/kakaoEx4", method=RequestMethod.POST)
	public String[] kakaoEx4Post(@RequestBody String address1) {
		//List<KakaoAreaVO> vos = studyService.getAddress2(address1);
		String[] address2 = studyService.getAddress2(address1);
		
		return address2;
	}
	
	// '도시'와 '지역'을 선택후 '검색'버튼을 클릭하면 해당지역의 주변 '카테고리'들을 검색시켜준다.
	@RequestMapping(value="/kakaoEx4Search", method=RequestMethod.POST)
	public String kakaoEx4SearchPost(Model model, 
			@RequestParam(name="address1", defaultValue="충청북도", required=false) String address1,
			@RequestParam(name="address2", defaultValue="청주시", required=false) String address2) {
		
		KakaoAreaVO vo = studyService.getAddressSearch(address1, address2);
		
		model.addAttribute("address1", vo.getAddress1());
		model.addAttribute("address2", vo.getAddress2());
		model.addAttribute("latitude", vo.getLatitude());
		model.addAttribute("longitude", vo.getLongitude());
		
		return "study/kakaomap/kakaoEx4Search";
	}
	
  // 카카오맵 응용하기5
	@RequestMapping(value="/kakaoEx5", method=RequestMethod.GET)
	public String kakaoEx5Get(Model model, String address) {
		if(address == null) address = "청주 사창사거리";
		model.addAttribute("address", address);
		return "study/kakaomap/kakaoEx5";
	}
	
  // 구글차트만들기
	@RequestMapping(value="/googleChart", method=RequestMethod.GET)
	public String googleChartGet(Model model,
			@RequestParam(name="part", defaultValue="bar", required=false) String part) {
		model.addAttribute("part", part);
		return "study/chart/chart";
	}
	
	// 구글차트만들기2 - 자료 입력하여 차크 만들기
	@RequestMapping(value="/googleChart2", method=RequestMethod.GET)
	public String googleChartGet2(Model model,
			@RequestParam(name="part", defaultValue="bar", required=false) String part) {
		model.addAttribute("part", part);
		return "study/chart2/chart";
	}
	
	@RequestMapping(value="/googleChart2", method=RequestMethod.POST)
	public String googleChart2Post(Model model,
			ChartVO vo) {
		model.addAttribute("vo", vo);
		return "study/chart2/chart";
	}
	
	// 최근 방문자수 차트로 표시하기
	@RequestMapping(value="/googleChart2Recently", method=RequestMethod.GET)
	public String googleChart2RecentlyGet(Model model,
			@RequestParam(name="part", defaultValue="line", required=false) String part) {
		//System.out.println("part : " + part);
		List<ChartVO> vos = null;
		if(part.equals("lineChartVisitCount")) {
			vos = studyService.getRecentlyVisitCount();
			// vos로 차트에서 처리가 잘 안되는것 같아서 다시 배열로 담아서 처리해본다.
			String[] visitDates = new String[7];
			int[] visitDays = new int[7];	// line차트는 x축과 y축이 모두 숫가자 와야하기에 날짜중에서 '일'만 담기로 한다.(정수타입으로)
			int[] visitCounts = new int[7];
			for(int i=0; i<7; i++) {
				visitDates[i] = vos.get(i).getVisitDate();
				visitDays[i] = Integer.parseInt(vos.get(i).getVisitDate().toString().substring(8));
				visitCounts[i] = vos.get(i).getVisitCount();
			}
			
			model.addAttribute("title", "최근 7일간 방문횟수");
			model.addAttribute("subTitle", "최근 7일동안 방문한 해당일자 방문자 총수를 표시합니다.");
			model.addAttribute("visitCount", "방문횟수");
			model.addAttribute("legend", "일일 방문 총횟수");
			model.addAttribute("part", part);
//			model.addAttribute("vos", vos);
			model.addAttribute("visitDates", visitDates);
			model.addAttribute("visitDays", visitDays);
			model.addAttribute("visitCounts", visitCounts);
		}
		
		return "study/chart2/chart";
	}
	
	// QR코드 생성하기 폼(URL 등록폼)
	@RequestMapping(value="/qrCode", method=RequestMethod.GET)
	public String qrCodeGet() {
		return "study/qrCode/qrCode";
	}
	
	// QR코드 생성하기 처리부분
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping(value="/qrCreate", method=RequestMethod.POST)
	public String barCreatePost(HttpServletRequest request, HttpSession session, String moveUrl) {
		String mid = (String) session.getAttribute("sMid");
		String uploadPath = request.getRealPath("/resources/data/qrCode/");
		String qrCodeName = studyService.qrCreate(mid, uploadPath, moveUrl);	// qr코드가 저장될 서버경로와 qr코드 찍었을때 이동할 url을 서비스객체로 넘겨서 qr코드를 생성하게 한다.
		
    return qrCodeName;
	}
	
}










