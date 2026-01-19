package com.monefy.entity;

import java.sql.Date;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Account {

	@Column(name = "account_id")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	private String name;

	private int currency;

	private String image;

	@Column(name = "initial_account_balance")
	private float initialAccountBalance;

	@Column(name = "initial_balance_date")
	private Date initialBalanceDate;

	private boolean enabled;

	private float balance;

	@Column(name = "user_id")
	private int userId;
}
