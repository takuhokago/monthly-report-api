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
@RequestMapping("departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // 所属一覧画面
    @GetMapping
    public String list(Model model) {
    	Department department = new Department();
    	model.addAttribute("department", department);
        model.addAttribute("departmentList", departmentService.findAll());

        return "departments/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(Department department, Model model) {
    	departmentService.save(department);
    	
        return "redirect:/departments";
    }

    // 従業員削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {


        return "redirect:/employees";
    }
    // 所属の削除処理
    @PostMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteById(String.valueOf(id));
        return "redirect:/departments"; // 削除後、一覧ページにリダイレクト
    }

}
