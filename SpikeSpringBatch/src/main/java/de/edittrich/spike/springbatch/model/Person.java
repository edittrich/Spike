package de.edittrich.spike.springbatch.model;

import java.io.Serializable;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Person implements Serializable {

	private static final long serialVersionUID = 1L;
	private String firstName;
    private String lastName;
}