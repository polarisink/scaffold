package github.polarisink.flow.controller;

import lombok.RequiredArgsConstructor;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报销demoController
 *
 * @author puhaiyang
 * @date 2018/12/19
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/expense")
public class ExpenseController {
  private final RuntimeService runtimeService;
  private final TaskService taskService;
  private final RepositoryService repositoryService;
  private final ProcessEngine processEngine;

/***************此处为业务代码******************/
  /**
   * 添加报销
   *
   * @param userId    用户Id
   * @param money     报销金额
   * @param descption 描述
   */
  @GetMapping("/add/{userId}/{money}/{descption}")
  @ResponseBody
  public String addExpense(@PathVariable String userId, @PathVariable Integer money, @PathVariable String descption) {
    //启动流程
    Map<String, Object> map = Map.of("taskUser", userId, "money", money);
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Expense", map);
    return "提交成功.流程Id为：" + processInstance.getId();
  }

  /**
   * 获取审批管理列表
   */
  @RequestMapping("/list/{userId}")
  @ResponseBody
  public Object list(@PathVariable String userId) {
    List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
    for (Task task : tasks) {
      System.out.println(task.toString());
    }
    return Arrays.stream(tasks.toArray()).map(Object::toString).collect(Collectors.joining(","));
  }

  /**
   * 批准
   *
   * @param taskId 任务ID
   */
  @RequestMapping("/apply/{taskId}")
  @ResponseBody
  public String apply(@PathVariable String taskId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (task == null) {
      throw new RuntimeException("流程不存在");
    }
    //通过审核
    taskService.complete(taskId, Map.of("outcome", "通过"));
    return "processed ok!";
  }

  /**
   * 拒绝
   */
  @ResponseBody
  @RequestMapping("/reject/{taskId}")
  public String reject(@PathVariable String taskId) {
    taskService.complete(taskId, Map.of("outcome", "驳回"));
    return "reject";
  }

  /**
   * 生成流程图
   *
   * @param processId 任务ID
   */
  @RequestMapping("/processDiagram/{processId}")
  public void genProcessDiagram(HttpServletResponse httpServletResponse, @PathVariable String processId) throws Exception {
    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
    //流程走完的不显示图
    if (pi == null) {
      return;
    }
    Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
    //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
    String InstanceId = task.getProcessInstanceId();
    List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(InstanceId).list();
    //得到正在执行的Activity的Id
    List<String> activityIds = new ArrayList<>();
    List<String> flows = new ArrayList<>();
    for (Execution exe : executions) {
      List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
      activityIds.addAll(ids);
    }
    //获取流程图
    BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
    ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
    ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
    InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0);
    byte[] buf = new byte[1024];
    int length;
    try (OutputStream out = httpServletResponse.getOutputStream();) {
      while ((length = in.read(buf)) != -1) {
        out.write(buf, 0, length);
      }
    }
  }
}
