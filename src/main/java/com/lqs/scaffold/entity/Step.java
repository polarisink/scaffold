package com.lqs.scaffold.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author lqs
 * @describe
 * @date 2021/11/20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Book对象", description = "书")
@Entity(name = "step")
@Table(name = "step")
public class Step implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@ApiModelProperty(value = "ID")
	@TableId(value = "id", type = IdType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "data", nullable = false, columnDefinition = "Text")
	private String data;

	private String name;
	private String mouldId;
	private String orderNum;
	private String assembly;
	private String selfCheck;
	private String specialCheck;
}
