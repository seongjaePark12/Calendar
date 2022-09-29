package com.spring.javagreenS.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.spring.javagreenS.dao.StudyDAO;
import com.spring.javagreenS.vo.ChartVO;
import com.spring.javagreenS.vo.KakaoAddressVO;
import com.spring.javagreenS.vo.KakaoAreaVO;
import com.spring.javagreenS.vo.OperatorVO;
import com.spring.javagreenS.vo.PersonVO;

@Service
public class StudyServiceImpl implements StudyService {

	@Autowired
	StudyDAO studyDAO;

	@Override
	public OperatorVO getOperator(String oid) {
		return studyDAO.getOperator(oid);
	}

	@Override
	public void setOperatorInputOk(OperatorVO vo) {
		// 1~50까지의 난수를 구해서 해당 난수의 hashKey값을 operatorHashTable2에서 가져와서 연산시켜준다.
		int keyIdx = (int) (Math.random()*50) + 1;
		
		// 비밀번호 암호화 시켜주는 메소드를 호출해서 처리한다.
		String strPwd = setPasswordEncoding(vo.getPwd(), keyIdx);
		
		vo.setPwd(strPwd);
		vo.setKeyIdx(keyIdx);
		
		studyDAO.setOperatorInputOk(vo);	// 잘 정리된 vo를 DB에 저장한다.
	}

	// 암호화 시켜주는 메소드
	private String setPasswordEncoding(String pwd, int keyIdx) {
		String hashKey = studyDAO.getOperatorHashKey(keyIdx);
		
	  // 입력된 비밀번호를 아스키코드로 변환하여 누적처리
		long intPwd;
		String strPwd = "";
		pwd = pwd.toUpperCase();
		
		for(int i=0; i<pwd.length(); i++) {
			intPwd = (long) pwd.charAt(i);
			strPwd += intPwd;
		}
		// 문자로 결합된 숫자를, 연산하기위해 다시 숫자로 변환한다.
		intPwd = Long.parseLong(strPwd);
		
		// 암호화를 위한 키 : hashKey
		long key = Integer.parseInt(hashKey, 16);
		long encPwd;
		
		// 암호화를 위한 XOR 연산하기
		encPwd = intPwd ^ key;
		strPwd = String.valueOf(encPwd);  // 암호화된 자료를 문자로 변환한다.
		
		return strPwd;
	}

	@Override
	public ArrayList<OperatorVO> getOperatorList() {
		return studyDAO.getOperatorList();
	}

	@Override
	public void setOperatorDelete(String oid) {
		studyDAO.setOperatorDelete(oid);
	}

	@Override
	public String setOperatorSearch(OperatorVO vo) {
		String oid = vo.getOid();
		String pwd = vo.getPwd();
		
		vo = studyDAO.getOperator(oid);
		
		if(vo == null) return "0";
		
		// 검색된 아이디가 존재한다면 입력받은 pwd를 암호화해서 다시 실제 DB의 pwd와 같은지를 판단처리
		String strPwd = setPasswordEncoding(pwd, vo.getKeyIdx());
		
		if(strPwd.equals(vo.getPwd())) return "1";
		
		return "0";
	}

	@Override
	public String[] getCityStringArr(String dodo) {
		String[] strArr = new String[100];
		
		if(dodo.equals("서울")) {
			strArr[0] = "강남구";
			strArr[1] = "강북구";
			strArr[2] = "서초구";
			strArr[3] = "도봉구";
			strArr[4] = "강동구";
			strArr[5] = "강서구";
			strArr[6] = "관악구";
			strArr[7] = "은평구";
			strArr[8] = "노은구";
			strArr[9] = "종로구";
		}
		else if(dodo.equals("경기")) {
			strArr[0] = "수원시";
			strArr[1] = "고양시";
			strArr[2] = "부천시";
			strArr[3] = "성남시";
			strArr[4] = "여주시";
			strArr[5] = "용인시";
			strArr[6] = "평택시";
			strArr[7] = "광명시";
			strArr[8] = "김포시";
			strArr[9] = "의정부시";
		}
		else if(dodo.equals("충북")) {
			strArr[0] = "청주시";
			strArr[1] = "충주시";
			strArr[2] = "제천시";
			strArr[3] = "진천군";
			strArr[4] = "괴산군";
			strArr[5] = "음성군";
			strArr[6] = "단양시";
			strArr[7] = "영동군";
			strArr[8] = "보은군";
			strArr[9] = "증평군";
		}
		else if(dodo.equals("충남")) {
			strArr[0] = "천안시";
			strArr[1] = "아산시";
			strArr[2] = "논산시";
			strArr[3] = "당진시";
			strArr[4] = "태안군";
			strArr[5] = "공주시";
			strArr[6] = "서천군";
			strArr[7] = "보령시";
			strArr[8] = "금산군";
			strArr[9] = "청양군";
		}
		return strArr;
	}

	@Override
	public ArrayList<String> getCityArrayListStr(String dodo) {
		ArrayList<String> vos = new ArrayList<String>();
		
		if(dodo.equals("서울")) {
			vos.add("강남구");
			vos.add("강북구");
			vos.add("서초구");
			vos.add("도봉구");
			vos.add("강동구");
			vos.add("강서구");
			vos.add("관악구");
			vos.add("은평구");
			vos.add("노은구");
			vos.add("종로구");
		}
		else if(dodo.equals("경기")) {
			vos.add("수원시");
			vos.add("고양시");
			vos.add("부천시");
			vos.add("성남시");
			vos.add("여주시");
			vos.add("용인시");
			vos.add("평택시");
			vos.add("광명시");
			vos.add("김포시");
			vos.add("의정부시");
		}
		else if(dodo.equals("충북")) {
			vos.add("청주시");
			vos.add("충주시");
			vos.add("제천시");
			vos.add("진천군");
			vos.add("괴산군");
			vos.add("음성군");
			vos.add("단양시");
			vos.add("영동군");
			vos.add("보은군");
			vos.add("증평군");
		}
		else if(dodo.equals("충남")) {
			vos.add("천안시");
			vos.add("아산시");
			vos.add("논산시");
			vos.add("당진시");
			vos.add("태안군");
			vos.add("공주시");
			vos.add("서천군");
			vos.add("보령시");
			vos.add("금산군");
			vos.add("청양군");
		}
		return vos;
	}

	@Override
	public ArrayList<OperatorVO> getOperatorVos(String oid) {
		return studyDAO.getOperatorVos(oid);
	}

	@Override
	public int fileUpload(MultipartFile fName) {
		int res = 0;
		try {
			UUID uid = UUID.randomUUID();
			String oFileName = fName.getOriginalFilename();
			String saveFileName = uid + "_" + oFileName;
			
			writeFile(fName, saveFileName);
			res = 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	private void writeFile(MultipartFile fName, String saveFileName) throws IOException {
		byte[] data = fName.getBytes();
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/images");
		
		FileOutputStream fos = new FileOutputStream(uploadPath + saveFileName);
		fos.write(data);
		fos.close();
	}

	@Transactional
	@Override
	public void setPersonInput(PersonVO vo) {
		studyDAO.setPersonInput(vo);
	}

	@Override
	public ArrayList<PersonVO> getPersonList() {
		return studyDAO.getPersonList();
	}

	@Override
	public void getCalendar() {
	  // model객체를 사용하게되면 불필요한 메소드가 많이 따라오기에 여기서는 request객체를 사용했다.
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		
		// 오늘 날짜 저장시켜둔다.(calToday변수, 년(toYear), 월(toMonth), 일(toDay))
		Calendar calToday = Calendar.getInstance();
		int toYear = calToday.get(Calendar.YEAR);
		int toMonth = calToday.get(Calendar.MONTH);
		int toDay = calToday.get(Calendar.DATE);
				
		// 화면에 보여줄 해당 '년(yy)/월(mm)'을 셋팅하는 부분(처음에는 오늘 년도와 월을 가져오지만, '이전/다음'버튼 클릭하면 해당 년과 월을 가져오도록 한다.
		Calendar calView = Calendar.getInstance();
		int yy = request.getParameter("yy")==null ? calView.get(Calendar.YEAR) : Integer.parseInt(request.getParameter("yy"));
	  int mm = request.getParameter("mm")==null ? calView.get(Calendar.MONTH) : Integer.parseInt(request.getParameter("mm"));
	  
	  if(mm < 0) { // 1월에서 전월 버튼을 클릭시에 실행
	  	yy--;
	  	mm = 11;
	  }
	  if(mm > 11) { // 12월에서 다음월 버튼을 클릭시에 실행
	  	yy++;
	  	mm = 0;
	  }
	  calView.set(yy, mm, 1);		// 현재 '년/월'의 1일을 달력의 날짜로 셋팅한다.
	  
	  int startWeek = calView.get(Calendar.DAY_OF_WEEK);  						// 해당 '년/월'의 1일에 해당하는 요일값을 숫자로 가져온다.
	  int lastDay = calView.getActualMaximum(Calendar.DAY_OF_MONTH);  // 해당월의 마지막일자(getActualMaxximum메소드사용)를 구한다.
	  
	  // 화면에 보여줄 년월기준 전년도/다음년도를 구하기 위한 부분
	  int prevYear = yy;  			// 전년도
	  int prevMonth = (mm) - 1; // 이전월
	  int nextYear = yy;  			// 다음년도
	  int nextMonth = (mm) + 1; // 다음월
	  
	  if(prevMonth == -1) {  // 1월에서 전월 버튼을 클릭시에 실행..
	  	prevYear--;
	  	prevMonth = 11;
	  }
	  
	  if(nextMonth == 12) {  // 12월에서 다음월 버튼을 클릭시에 실행..
	  	nextYear++;
	  	nextMonth = 0;
	  }
	  
	  // 현재달력에서 앞쪽의 빈공간은 '이전달력'을 보여주고, 뒷쪽의 남은공간은 '다음달력'을 보여주기위한 처리부분(아래 6줄)
	  Calendar calPre = Calendar.getInstance(); // 이전달력
	  calPre.set(prevYear, prevMonth, 1);  			// 이전 달력 셋팅
	  int preLastDay = calPre.getActualMaximum(Calendar.DAY_OF_MONTH);  // 해당월의 마지막일자를 구한다.
	  
	  Calendar calNext = Calendar.getInstance();// 다음달력
	  calNext.set(nextYear, nextMonth, 1);  		// 다음 달력 셋팅
	  int nextStartWeek = calNext.get(Calendar.DAY_OF_WEEK);  // 다음달의 1일에 해당하는 요일값을 가져온다.
	  
	  /* ---------  아래는  앞에서 처리된 값들을 모두 request객체에 담는다.  -----------------  */
	  
	  // 오늘기준 달력...
	  request.setAttribute("toYear", toYear);
	  request.setAttribute("toMonth", toMonth);
	  request.setAttribute("toDay", toDay);
	  
	  // 화면에 보여줄 해당 달력...
	  request.setAttribute("yy", yy);
	  request.setAttribute("mm", mm);
	  request.setAttribute("startWeek", startWeek);
	  request.setAttribute("lastDay", lastDay);
	  
	  // 화면에 보여줄 해당 달력 기준의 전년도, 전월, 다음년도, 다음월 ...
	  request.setAttribute("preYear", prevYear);
		request.setAttribute("preMonth", prevMonth);
		request.setAttribute("nextYear", nextYear);
		request.setAttribute("nextMonth", nextMonth);
		
		// 현재 달력의 '앞/뒤' 빈공간을 채울, 이전달의 뒷부분과 다음달의 앞부분을 보여주기위해 넘겨주는 변수
		request.setAttribute("preLastDay", preLastDay);				// 이전달의 마지막일자를 기억하고 있는 변수
		request.setAttribute("nextStartWeek", nextStartWeek);	// 다음달의 1일에 해당하는 요일을 기억하고있는 변수
	}

	@Override
	public KakaoAddressVO getAddressName(String address) {
		return studyDAO.getAddressName(address);
	}

	@Override
	public void setAddressName(KakaoAddressVO vo) {
		studyDAO.setAddressName(vo);
	}

	@Override
	public List<KakaoAddressVO> getAddressNameList() {
		return studyDAO.getAddressNameList();
	}

	@Override
	public void kakaoEx2Delete(String address) {
		studyDAO.kakaoEx2Delete(address);
	}

	@Override
	public String[] getAddress1() {
		return studyDAO.getAddress1();
	}

	@Override
	public String[] getAddress2(String address1) {
		return studyDAO.getAddress2(address1);
	}

	@Override
	public KakaoAreaVO getAddressSearch(String address1, String address2) {
		return studyDAO.getAddressSearch(address1, address2);
	}

	@Override
	public List<ChartVO> getRecentlyVisitCount() {
		return studyDAO.getRecentlyVisitCount();
	}

	@Override
	public String qrCreate(String mid, String uploadPath, String moveUrl) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
		UUID uid = UUID.randomUUID();
		String strUid = uid.toString().substring(0,4);
		String qrCodeName = "";
		
		if(moveUrl.indexOf("@") == -1) {	// 이동할 주소로 넘어올때의 처리
			qrCodeName = sdf.format(new Date()) + "_" + mid + "_" + strUid;
		}
		else {		// 개인정보(이메일주소)로 넘어올때의 처리
			qrCodeName = sdf.format(new Date()) + "_" + mid + "_" + moveUrl + "_" + strUid;
		}
	  try {
	      File file = new File(uploadPath);		// qr코드 이미지를 저장할 디렉토리 지정
	      if(!file.exists()) {
	          file.mkdirs();
	      }
	      String codeurl = new String(moveUrl.getBytes("UTF-8"), "ISO-8859-1");	// qr코드 인식시 이동할 url 주소
	      //int qrcodeColor = 0xFF2e4e96;			// qr코드 바코드 생성값(전경색)
	      int qrcodeColor = 0xFF000000;			// qr코드 바코드 생성값(전경색) - 뒤의 6자리가 색상코드임
	      int backgroundColor = 0xFFFFFFFF;	// qr코드 배경색상값
	      
	      QRCodeWriter qrCodeWriter = new QRCodeWriter();
	      BitMatrix bitMatrix = qrCodeWriter.encode(codeurl, BarcodeFormat.QR_CODE,200, 200);
	      
	      MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(qrcodeColor,backgroundColor);
	      BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix,matrixToImageConfig);
	      
	      ImageIO.write(bufferedImage, "png", new File(uploadPath + qrCodeName + ".png"));		// ImageIO를 사용한 바코드 파일쓰기
	      
	      // qr코드 생성후 정보를 DB에 저장하기(신상내역으로 보낸것들만 저장하려함 - 나중에 본인이 생성된 qr코드 가져왔을때 DB에 있는 정보와 일치하는지 알아보기 위함)
	      if(qrCodeName.indexOf("@") != -1) studyDAO.setQrCreate(qrCodeName);
	  } catch (Exception e) {
	      e.printStackTrace();
	  }
	  return qrCodeName;
	}	

}

























