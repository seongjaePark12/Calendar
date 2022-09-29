package com.spring.javagreenS;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/calendar")
public class CalendarController {
	@Autowired
	CalendarService calendarService;
	
  // 달력내역 가져오기
	@RequestMapping(value="/calendar", method=RequestMethod.GET)
	public String calendarGet() {
		calendarService.getCalendar();
		return "calendar/calendar/calendar";
	}

}










