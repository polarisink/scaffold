package com.lqs.scaffold.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * <p>
 * 书
 * </p>
 *
 * @author polaris
 * @since 2021-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Book对象", description = "书")
@Entity(name = "book")
public class Books implements Serializable, Comparable {

	private static final long serialVersionUID = 1L;

	@Id
	@Schema(name = "ID")
	@TableId(value = "id", type = IdType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Schema(name = "书名")
	private String name;

	@Schema(name = "数量")
	private Integer count;


	@Override
	public int compareTo(Object o) {
		return id.intValue();
	}
}
