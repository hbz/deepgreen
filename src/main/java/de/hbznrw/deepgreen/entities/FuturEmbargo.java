package de.hbznrw.deepgreen.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "futur_embargo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuturEmbargo {
	
	@Id
	@Column(name = "notification_id", nullable = false)
	private String notificationId;
	
	@Column(name = "date", nullable = false)
	private String date;

}
