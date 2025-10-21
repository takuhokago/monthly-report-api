package com.kagoshima.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.kagoshima.entity.Department;
import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Employee.Role;
import com.kagoshima.entity.Report;
import com.kagoshima.service.EmployeeService;
import com.kagoshima.service.ExcelService;
import com.kagoshima.service.ReportService;
import com.kagoshima.service.UserDetail;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.kagoshima.api.dto.ReportDto;

@WebMvcTest(controllers = ReportApiController.class, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { com.kagoshima.SecurityConfig.class,
				com.kagoshima.security.JwtAuthenticationFilter.class }) })
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
class ReportApiControllerTest {

	@Autowired
	MockMvc mvc;

	// モック化した依存コンポーネント
	@MockBean
	ReportService reportService;
	@MockBean
	EmployeeService employeeService;
	@MockBean
	ExcelService excelService;

	private RequestPostProcessor withUser(Employee e) {
		var ud = new UserDetail(e);
		var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
		return SecurityMockMvcRequestPostProcessors.authentication(auth);
	}

	private Department dept(String name) {
		var d = new Department();
		d.setId(1);
		d.setName(name);
		return d;
	}

	private Employee admin() {
		var e = new Employee();
		e.setCode("9999");
		e.setRole(Role.ADMIN);
		e.setLastName("山田");
		e.setFirstName("太郎");
		e.setPassword("x");
		e.setEmail("admin@example.com");
		e.setCreatedAt(LocalDateTime.now());
		e.setUpdatedAt(LocalDateTime.now());
		e.setDeleteFlg(false);
		e.setDepartment(dept("総務"));
		return e;
	}

	private Employee user(String code) {
		var e = new Employee();
		e.setCode(code);
		e.setRole(Role.GENERAL);
		e.setLastName("佐藤");
		e.setFirstName("花子");
		e.setPassword("x");
		e.setEmail(code + "@example.com");
		e.setCreatedAt(LocalDateTime.now());
		e.setUpdatedAt(LocalDateTime.now());
		e.setDeleteFlg(false);
		e.setDepartment(dept("営業"));
		return e;
	}

	private Report report(String id, Employee owner, YearMonth ym) {
		var r = new Report();
		r.setId(Integer.valueOf(id));
		r.setEmployee(owner);
		r.setReportMonth(ym);
		r.setReportDeadline(ym.atEndOfMonth());
		r.setDeleteFlg(false);
		r.setCompleteFlg(false);
		return r;
	}

	// =========================
	// GET /api/reports のテスト
	// =========================
	@Nested
	@DisplayName("GET /api/reports")
	class GetReports {

		@Test
		@DisplayName("管理者: 全件取得・dateSetは降順・isPastCheck=true")
		void adminGetsAll_descSorted_hasPast() throws Exception {
			var a = admin();
			var u1 = user("9999");
			var r1 = report("1", u1, YearMonth.of(2025, 9));
			var r2 = report("2", u1, YearMonth.of(2025, 8));

			// スタブ
			given(reportService.findAll()).willReturn(List.of(r1, r2));
			given(reportService.findByEmployee(any())).willReturn(List.of(r1));

			mvc.perform(get("/api/reports").with(withUser(a))).andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					// 件数 (listSize)
					.andExpect(jsonPath("$.listSize").value(2))
					// reportList 配列の長さ
					.andExpect(jsonPath("$.reportList.length()").value(2))
					// 並び（今回は r1が先、r2が後を想定）
					.andExpect(jsonPath("$.reportList[0].id").value(1))
					.andExpect(jsonPath("$.reportList[0].reportMonth").value("2025-09"))
					.andExpect(jsonPath("$.reportList[1].reportMonth").value("2025-08"))
					// dateSet は降順（"2025-09", "2025-08"）
					.andExpect(jsonPath("$.dateSet[0]").value("2025-09"))
					.andExpect(jsonPath("$.dateSet[1]").value("2025-08"))
					// isPastCheck
					.andExpect(jsonPath("$.isPastCheck").value(true));

			// 呼び出し検証
			verify(reportService, times(1)).findAll();
			verify(reportService, times(1)).findByEmployee(any());
			verifyNoMoreInteractions(reportService);
		}

		@Test
		@DisplayName("一般: 自分の分のみ取得・dateSetは降順・isPastCheck=true")
		void generalGetsOwn_descSorted_hasPast() throws Exception {
			var u = user("1234");
			var r1 = report("10", u, YearMonth.of(2025, 9));
			var r2 = report("11", u, YearMonth.of(2025, 8));

			// コントローラ実装は findByEmployee(...) を2回呼ぶ（一覧 と isPastCheck 用）
			given(reportService.findByEmployee(any())).willReturn(List.of(r1, r2)); // 1回目: 一覧用（非空）／2回目:
																					// isPastCheck用（非空）

			mvc.perform(get("/api/reports").with(withUser(u))).andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					// 件数
					.andExpect(jsonPath("$.listSize").value(2))
					// 配列長
					.andExpect(jsonPath("$.reportList.length()").value(2))
					// 中身の一部
					.andExpect(jsonPath("$.reportList[0].reportMonth").value("2025-09"))
					.andExpect(jsonPath("$.reportList[1].reportMonth").value("2025-08"))
					// 年月リストは降順
					.andExpect(jsonPath("$.dateSet[0]").value("2025-09"))
					.andExpect(jsonPath("$.dateSet[1]").value("2025-08"))
					// 過去レポートあり
					.andExpect(jsonPath("$.isPastCheck").value(true));

			// 呼び出し検証：一般ユーザーは findAll() を呼ばない
			verify(reportService, times(2)).findByEmployee(any()); // 一覧 + isPastCheck
			verify(reportService, times(0)).findAll();
			verifyNoMoreInteractions(reportService);
		}

		@Test
		@DisplayName("一般: 0件の場合・dateSetは空・isPastCheck=false")
		void generalGetsOwn_empty_noPast() throws Exception {
			var u = user("1234");

			// 1回目（一覧）も2回目（isPastCheck）も空を返す
			given(reportService.findByEmployee(any())).willReturn(List.of(), List.of());

			mvc.perform(get("/api/reports").with(withUser(u))).andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.listSize").value(0)).andExpect(jsonPath("$.reportList.length()").value(0))
					.andExpect(jsonPath("$.dateSet.length()").value(0))
					.andExpect(jsonPath("$.isPastCheck").value(false));

			verify(reportService, times(2)).findByEmployee(any()); // 一覧 + isPastCheck
			verify(reportService, times(0)).findAll();
			verifyNoMoreInteractions(reportService);
		}
	}

	// ================================
	// GET /api/reports/{id} のテスト
	// ================================
	@Nested
	@DisplayName("GET /api/reports/{id}")
	class GetReportById {

		@Test
		@DisplayName("指定IDのレポートを1件取得できる")
		void getById_ok_admin() throws Exception {
			var a = admin();
			var owner = user("1234");
			var r = report("1", owner, YearMonth.of(2025, 9));

			// スタブ
			given(reportService.findById("1")).willReturn(r);

			mvc.perform(get("/api/reports/{id}", "1").with(withUser(a))).andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					// レスポンスボディ（ReportResponse）内の report フィールドを検証
					.andExpect(jsonPath("$.report.id").value(1))
					.andExpect(jsonPath("$.report.reportMonth").value("2025-09"));

			// 呼び出し検証
			verify(reportService, times(1)).findById("1");
			verifyNoMoreInteractions(reportService);
		}
	}

	// ===========================
	// POST /api/reports のテスト
	// ===========================
	@Nested
	@DisplayName("POST /api/reports")
	class CreateReport {

		@Test
		@DisplayName("正常系: 新規作成に成功し、ReportResponse(JSON)を返す")
		void create_ok() throws Exception {
			var u = user("1234");

			// 入力JSON（最低限）
			String reqJson = """
					{
					  "reportMonth": "2025-10",
					  "contentBusiness": "新規開拓と保守対応を実施",
					  "completeFlg": false
					}
					""";

			// save() が返すダミーReport
			var saved = report("101", u, YearMonth.of(2025, 10));
			saved.setCompleteFlg(false);

			// スタブ
			given(reportService.save(any(ReportDto.class), any(Employee.class))).willReturn(saved);

			mvc.perform(post("/api/reports").with(withUser(u)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
					.content(reqJson)).andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					// Controllerは new ReportResponse(ReportMapper.toDto(saved)) を返す想定なので
					// $.report.* の形で検証
					.andExpect(jsonPath("$.report.id").value(101))
					.andExpect(jsonPath("$.report.reportMonth").value("2025-10"))
					.andExpect(jsonPath("$.report.completeFlg").value(false));

			verify(reportService, times(1)).save(any(ReportDto.class), any(Employee.class));
			verifyNoMoreInteractions(reportService);
		}

		@Test
		@DisplayName("異常系: save() が RuntimeException を投げた場合は 400 と {message} を返す")
		void create_badRequest_whenServiceThrows() throws Exception {
			var u = user("1234");

			String reqJson = """
					{
					  "reportMonth": "2025-10",
					  "contentBusiness": "重複登録を試行"
					}
					""";

			given(reportService.save(any(ReportDto.class), any(Employee.class)))
					.willThrow(new RuntimeException("既に当月のレポートが存在します"));

			mvc.perform(post("/api/reports").with(withUser(u)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
					.content(reqJson)).andExpect(status().isBadRequest())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.message").value("既に当月のレポートが存在します"));

			verify(reportService, times(1)).save(any(ReportDto.class), any(Employee.class));
			verifyNoMoreInteractions(reportService);
		}

		@Test
		@DisplayName("引数検証: save() に渡される Employee は認証ユーザーと一致する")
		void create_passesAuthenticatedEmployeeToService() throws Exception {
			var u = user("5678");

			String reqJson = """
					{ "reportMonth": "2025-09" }
					""";

			var saved = report("201", u, YearMonth.of(2025, 9));
			given(reportService.save(any(ReportDto.class), any(Employee.class))).willReturn(saved);

			mvc.perform(post("/api/reports").with(withUser(u)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
					.content(reqJson)).andExpect(status().isOk());

			// Employee 引数をキャプチャして認証ユーザーと一致するか検証
			var dtoCaptor = org.mockito.ArgumentCaptor.forClass(ReportDto.class);
			var empCaptor = org.mockito.ArgumentCaptor.forClass(Employee.class);
			verify(reportService).save(dtoCaptor.capture(), empCaptor.capture());

			var passedEmp = empCaptor.getValue();
			org.assertj.core.api.Assertions.assertThat(passedEmp.getCode()).isEqualTo("5678");

			verifyNoMoreInteractions(reportService);
		}
	}

}
