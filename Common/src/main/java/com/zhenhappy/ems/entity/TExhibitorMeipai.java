package com.zhenhappy.ems.entity;

// Generated 2014-7-22 15:33:43 by Hibernate Tools 3.4.0.CR1

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TExhibitorMeipai generated by hbm2java
 */
@Entity
@Table(name = "t_exhibitor_meipai"
	, schema = "dbo"
)
public class TExhibitorMeipai implements java.io.Serializable {

	private Integer id;
	private int exhibitorId;
	private String meipai;
    private String meipaiEn;
	private Date createTime;
	private Date updateTime;
	private Integer updateAdmin;
	private Date adminUpdateTime;

	public TExhibitorMeipai() {
	}

	public TExhibitorMeipai(Integer id, int exhibitorId, String meipai) {
		this.id = id;
		this.exhibitorId = exhibitorId;
		this.meipai = meipai;
	}

	public TExhibitorMeipai(int exhibitorId, String meipai, Date createTime,
			Date updateTime, Integer updateAdmin, Date adminUpdateTime) {
		super();
		this.exhibitorId = exhibitorId;
		this.meipai = meipai;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.updateAdmin = updateAdmin;
		this.adminUpdateTime = adminUpdateTime;
	}

	public TExhibitorMeipai(Integer id, int exhibitorId, String meipai,
			Date createTime, Date updateTime, Integer updateAdmin,
			Date adminUpdateTime) {
		super();
		this.id = id;
		this.exhibitorId = exhibitorId;
		this.meipai = meipai;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.updateAdmin = updateAdmin;
		this.adminUpdateTime = adminUpdateTime;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "exhibitor_id", nullable = false)
	public int getExhibitorId() {
		return this.exhibitorId;
	}

    @Column(name = "meipai_en",nullable = true)
    public String getMeipaiEn() {
        return meipaiEn;
    }

    public void setMeipaiEn(String meipaiEn) {
        this.meipaiEn = meipaiEn;
    }

    public void setExhibitorId(int exhibitorId) {
		this.exhibitorId = exhibitorId;
	}

	@Column(name = "meipai", nullable = false, length = 1000)
	public String getMeipai() {
		return this.meipai;
	}

	public void setMeipai(String meipai) {
		this.meipai = meipai;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 23)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time", length = 23)
	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "update_admin")
	public Integer getUpdateAdmin() {
		return this.updateAdmin;
	}

	public void setUpdateAdmin(Integer updateAdmin) {
		this.updateAdmin = updateAdmin;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "admin_update_time", length = 23)
	public Date getAdminUpdateTime() {
		return this.adminUpdateTime;
	}

	public void setAdminUpdateTime(Date adminUpdateTime) {
		this.adminUpdateTime = adminUpdateTime;
	}

}
