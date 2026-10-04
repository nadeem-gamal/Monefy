package com.monefy.entity;

import java.sql.Date;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
public class Transfer {

	@Column(name = "transfer_id")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	private Date date;

	@Column(name = "from_account_id")
	private int fromAccountId;

	@Column(name = "to_account_id")
	private int toAccountId;

	private float amount;

	private String description;

	public Transfer(Long id, Date date, int fromAccountId, int toAccountId, float amount, String description) {
		this.id = id;
		this.date = date;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.amount = amount;
		this.description = description;
	}

	public Transfer(long id, Date date, int fromAccountId, int toAccountId, int amount, String description) {
		this.id = id;
		this.date = date;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.amount = amount;
		this.description = description;
	}
}
