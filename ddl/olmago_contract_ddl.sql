docker run -it mysql:5.7 mysql

alter database olmago_contract default character set utf8;

    alter table dc_sub
       drop
       foreign key dc_sub_fk_dc_plcy_id;

    alter table dc_sub
       drop
       foreign key dc_sub_fk_prod_sub_id
        ;

    alter table package
       drop
       foreign key package_fk_opt_cntrct_id
 ;

    alter table package
       drop
       foreign key package_fk_pkg_cntrct_id
    ;

    alter table prod_sub
       drop
       foreign key prod_sub_fk_contract_id
;

    alter table prod_sub
       drop
       foreign key prod_sub_fk_product_code
       ;

drop table if exists contract CASCADE;
drop table if exists dc_plcy CASCADE; 
drop table if exists dc_sub CASCADE;
drop table if exists message_in_box CASCADE ;
drop table if exists msg_envelope CASCADE ;
drop table if exists package CASCADE ;
drop table if exists prod CASCADE ;
drop table if exists prod_rel CASCADE; 
drop table if exists prod_sub CASCADE;

create table contract (
    id bigint not null auto_increment,
    bf_last_pay_dtm datetime(6),
    bill_period varchar(255),
    cur_bill_end_dt date,
    cur_bill_sta_dt date,
    month_passed integer,
    fst_bill_sta_dt date,
    cntrct_typ varchar(20) not null,
    cust_id bigint not null,
    fee_prod_cd varchar(10) not null,
    last_ord_id bigint,
    last_pay_dtm datetime(6),
    sub_rcv_cncl_dtm datetime(6),
    term_rcv_cncl_dtm datetime(6),
    sub_cmpl_dtm datetime(6),
    sub_rcv_dtm datetime(6),
    term_cmpl_dtm datetime(6),
    term_rcv_dtm datetime(6),
    unit_cnvt_dtm datetime(6),
    version integer not null,
    primary key (id)
) engine=InnoDB;


create table dc_plcy (
   dc_plcy_cd varchar(10) not null,
    copn_plcy_cd varchar(40),
    dc_amt_or_rate integer not null,
    dc_prd_typ varchar(20) not null,
    dc_plcy_nm varchar(80) not null,
    dc_sta_dt_typ varchar(20) not null,
    dc_typ varchar(40) not null,
    dc_unit varchar(10) not null,
    primary key (dc_plcy_cd)
) engine=InnoDB;
alter table dc_plcy convert to character set utf8;

create table dc_sub (
   id bigint not null auto_increment,
    copn_id varchar(100),
    dc_end_dt date not null,
    dc_end_rgst_dt date,
    dc_rgst_dt date not null,
    dc_sta_dt date not null,
    sub_rcv_cncl_dtm datetime(6),
    term_rcv_cncl_dtm datetime(6),
    sub_cmpl_dtm datetime(6),
    sub_rcv_dtm datetime(6),
    term_cmpl_dtm datetime(6),
    term_rcv_dtm datetime(6),
    version integer not null,
    dc_plcy_id varchar(10),
    prod_sub_id bigint,
    primary key (id)
) engine=InnoDB;


create table message_in_box (
   id varchar(255) not null,
    event_type varchar(255) not null,
    payload varchar(255) not null,
    received_date_time datetime(6) not null,
    primary key (id)
) engine=InnoDB;


create table msg_envelope (
   id bigint not null auto_increment,
    agg_id varchar(255) not null,
    agg_typ varchar(255) not null,
    bind_nm varchar(255) not null,
    created_at datetime(6) not null,
    event_typ varchar(255) not null,
    payload varchar(2048) not null,
    published boolean not null,
    published_at datetime(6),
    uuid varchar(255) not null,
    primary key (id)
) engine=InnoDB;


create table package (
   id bigint not null auto_increment,
    sub_rcv_cncl_dtm datetime(6),
    term_rcv_cncl_dtm datetime(6),
    sub_cmpl_dtm datetime(6),
    sub_rcv_dtm datetime(6),
    term_cmpl_dtm datetime(6),
    term_rcv_dtm datetime(6),
    opt_cntrct_id bigint,
    pkg_cntrct_id bigint,
    version integer not null,
    primary key (id)
) engine=InnoDB;


create table prod (
   prod_cd varchar(10) not null,
    avail_prod_typ varchar(40) not null,
    bill_prd varchar(20) not null,
    fee_vat_incl integer not null,
    fst_sub_dc_tgt boolean not null,
    prod_nm varchar(80) not null,
    primary key (prod_cd)
) engine=InnoDB;
alter table prod convert to character set utf8;

create table prod_rel (
   id bigint not null auto_increment,
    end_dt date not null,
    main_prod_cd varchar(10) not null,
    prod_rel_typ varchar(40) not null,
    sta_dt date not null,
    sub_prod_cd varchar(10) not null,
    primary key (id)
) engine=InnoDB;


create table prod_sub (
   id bigint not null auto_increment,
    sub_rcv_cncl_dtm datetime(6),
    term_rcv_cncl_dtm datetime(6),
    sub_cmpl_dtm datetime(6),
    sub_rcv_dtm datetime(6),
    term_cmpl_dtm datetime(6),
    term_rcv_dtm datetime(6),
    version integer not null,
    contract_id bigint,
    prod_cd varchar(10),
    primary key (id)
) engine=InnoDB;
;

alter table dc_sub
   add constraint dc_sub_fk_dc_plcy_id
   foreign key (dc_plcy_id)
   references dc_plcy (dc_plcy_cd)
   ;


alter table dc_sub
   add constraint dc_sub_fk_prod_sub_id
   foreign key (prod_sub_id)
   references prod_sub (id)
;

alter table package
   add constraint package_fk_opt_cntrct_id
   foreign key (opt_cntrct_id)
   references contract (id)
;

alter table package
   add constraint package_fk_pkg_cntrct_id
   foreign key (pkg_cntrct_id)
   references contract (id)
;

alter table prod_sub
   add constraint prod_sub_fk_contract_id
   foreign key (contract_id)
   references contract (id)
;

alter table prod_sub
   add constraint prod_sub_fk_product_code
   foreign key (prod_cd)
   references prod (prod_cd)
;