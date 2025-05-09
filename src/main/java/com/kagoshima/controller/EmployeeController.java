package com.kagoshima.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kagoshima.constants.ErrorKinds;
import com.kagoshima.constants.ErrorMessage;
import com.kagoshima.entity.Department;
import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Employee.Role;
import com.kagoshima.entity.Report;
import com.kagoshima.service.DepartmentService;
import com.kagoshima.service.EmployeeService;
import com.kagoshima.service.ReportService;
import com.kagoshima.service.UserDetail;

@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ReportService reportService;
    private final DepartmentService departmentService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, ReportService reportService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.reportService = reportService;
        this.departmentService = departmentService;
    }

    // 従業員一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        // 一般権限の場合月報一覧に遷移
        if (userDetail.getEmployee().getRole().equals(Role.GENERAL))
            return "redirect:/reports";

        model.addAttribute("listSize", employeeService.findAll().size());
        model.addAttribute("employeeList", employeeService.findAll());

        return "employees/roleAdm/list";
    }

    // 従業員詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable String code, Model model) {

        model.addAttribute("employee", employeeService.findByCode(code));
        return "employees/roleAdm/detail";
    }

    // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Employee employee, Model model) {
    	List<Department> departmentList = new ArrayList<>();
    	departmentList = departmentService.findAll();
    	for(Department department : departmentList) {
    		System.out.println(department.getName());
    	}
    	model.addAttribute("departmentList", departmentList);
    	
        return "employees/roleAdm/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Employee employee, BindingResult res, Model model) {

        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         */
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));

            return create(employee, model);

        }

        // 入力チェック
        if (res.hasErrors()) {
            return create(employee, model);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee, model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee, model);
        }

        return "redirect:/employees";
    }

    // 従業員削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = employeeService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employee", employeeService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/employees";
    }

    // 従業員更新画面
    @GetMapping(value = "/update/{code}/")
    public String change(@PathVariable String code, Model model, Employee employee) {
        if (employee.getFirstName() == null) {
            model.addAttribute("employee", employeeService.findByCode(code));
        } else {
            model.addAttribute("employee", employee);
        }
        
        List<Department> departmentList = new ArrayList<>();
    	departmentList = departmentService.findAll();
    	for(Department department : departmentList) {
    		System.out.println(department.getName());
    	}
    	model.addAttribute("departmentList", departmentList);
        
        return "employees/roleAdm/update";
    }

    // 従業員更新処理
    @PostMapping(value = "/update")
    public String update(@Validated Employee employee, BindingResult res, Model model,
            @AuthenticationPrincipal UserDetail userDetail) {

        // 入力チェック
        if (res.hasErrors()) {
            return change(employee.getCode(), model, employee);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.update(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return change(employee.getCode(), model, employee);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return change(employee.getCode(), model, employee);
        }

        if (userDetail.getEmployee().getRole().equals(Role.ADMIN)) {
            // 管理者権限の場合、従業員一覧に遷移
            return "redirect:/employees";
        } else {
            // 一般権限の場合、日報一覧に遷移
            return "redirect:/reports";
        }
    }

    // プロフィール
    @GetMapping(value = "/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        // プロフィール更新後再度プロフィール画面遷移時に最新のEmployeeデータを表示させる
        Employee user = employeeService.findByCode(userDetail.getEmployee().getCode());
        List<Report> userReports = reportService.findByEmployee(user);

        model.addAttribute("employee", user);
        model.addAttribute("reportList", userReports);
        model.addAttribute("listSize", userReports.size());
        
        List<Department> departmentList = new ArrayList<>();
    	departmentList = departmentService.findAll();
    	for(Department department : departmentList) {
    		System.out.println(department.getName());
    	}
    	model.addAttribute("departmentList", departmentList);

        return "employees/roleGen/profile";
    }

}
