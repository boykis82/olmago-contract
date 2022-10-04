insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMP0000001','PACKAGE','MONTHLY',9900,1,'우주패스All');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMP0000002','PACKAGE','MONTHLY',9900,1,'우주패스Life');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMP0000003','PACKAGE','MONTHLY',4900,1,'우주패스Mini');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMP0000004','PACKAGE','MONTHLY',2900,1,'우주패스Slim');

insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMB0000001','BASIC_BENEFIT','MONTHLY',2000,0,'Google one(all)');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMB0000002','BASIC_BENEFIT','MONTHLY',2000,0,'아마존 무료배송');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMB0000003','BASIC_BENEFIT','MONTHLY',0,0,'세븐일레븐');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMB0000004','BASIC_BENEFIT','MONTHLY',0,0,'투썸플레이스');

insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000001','OPTION','MONTHLY',5000,1,'배달의민족');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000002','OPTION','MONTHLY',5000,1,'굽네치킨');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000003','OPTION','MONTHLY',9900,1,'wavve and data');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000004','OPTION','MONTHLY',7900,1,'flo and data');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000005','OPTION','MONTHLY',11900,1,'게임패스 얼티밋');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000006','OPTION','MONTHLY',1000,1,'Google one(mini)');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000007','OPTION','MONTHLY',12300,1,'Wavve and data plus');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000008','OPTION','MONTHLY',9000,1,'Flo and data plus');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000009','OPTION','MONTHLY',15900,1,'Wavve and data premium');
insert into prod (prod_cd,avail_prod_typ,bill_prd,fee_vat_incl,fst_sub_dc_tgt,prod_nm) values ('NMO0000010','OPTION','MONTHLY',5000,0,'야놀자');

insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCP0000001',null,8900,'ONE_MONTH','우주패스All_Life_Standard_최초가입할인','IMMEDIATELY','THE_FIRST_SUBSCRIPTION','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCP0000002',null,4800,'ONE_MONTH','우주패스Mini_최초가입할인','IMMEDIATELY','THE_FIRST_SUBSCRIPTION','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCP0000003',null,2800,'ONE_MONTH','우주패스Slim_최초가입할인','IMMEDIATELY','THE_FIRST_SUBSCRIPTION','AMOUNT');

insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCB0000001',null,100,'INFINITE','패키지 기본혜택 100%할인','IMMEDIATELY','BASIC_BENEFIT_FREE','RATE');

insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCO0000001',null,7900,'INFINITE','Flo 옵션 7900원 할인','IMMEDIATELY','OPTION','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCO0000002',null,6000,'INFINITE','게임패스 옵션 6000원 할인','IMMEDIATELY','OPTION','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCO0000003',null,5000,'INFINITE','배민 옵션 5000원 할인','IMMEDIATELY','OPTION','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCO0000004',null,5000,'INFINITE','야놀자 옵션 5000원 할인','IMMEDIATELY','OPTION','AMOUNT');

insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCU0000001',null,11800,'ONE_MONTH','게임패스 얼티밋 단품_최초가입할인','IMMEDIATELY','THE_FIRST_SUBSCRIPTION','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCU0000002',null,7800,'ONE_MONTH','Flo and Data 단품_최초가입할인','IMMEDIATELY','THE_FIRST_SUBSCRIPTION','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCU0000003',null,8900,'ONE_MONTH','Flo and Data 플러스 단품_최초가입할인','IMMEDIATELY','THE_FIRST_SUBSCRIPTION','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCU0000004',null,7900,'INFINITE','Flo and Data 단품_이동전화요금제할인(프라임플러스,플래티넘,맥스)','IMMEDIATELY','MOBILE_PHONE_PRICE_PLAN_LINKED','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCU0000005',null,5530,'INFINITE','Flo and Data 단품_이동전화요금제할인(프라임,스페셜)','IMMEDIATELY','MOBILE_PHONE_PRICE_PLAN_LINKED','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCU0000006',null,9000,'INFINITE','Flo and Data 플러스 단품_이동전화요금제할인(플래티넘)','IMMEDIATELY','MOBILE_PHONE_PRICE_PLAN_LINKED','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCU0000007',null,7900,'INFINITE','Flo and Data 플러스 단품_이동전화요금제할인(프라임플러스,맥스)','IMMEDIATELY','MOBILE_PHONE_PRICE_PLAN_LINKED','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCU0000008',null,5530,'INFINITE','Flo and Data 플러스 단품_이동전화요금제할인(프라임,스페셜)','IMMEDIATELY','MOBILE_PHONE_PRICE_PLAN_LINKED','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCU0000009',null,4900,'ONE_MONTH','배달의민족 단품_최초가입할인','IMMEDIATELY','THE_FIRST_SUBSCRIPTION','AMOUNT');

insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCC0000001','CP00000001',100,'ONE_MONTH','쿠폰할인1','IMMEDIATELY','COUPON','RATE');

insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCM0000001',null,9900,'INFINITE','이동전화 요금제 연계(all/life) 100%할인(프라임플러스이상)','IMMEDIATELY','MOBILE_PHONE_PRICE_PLAN_LINKED','AMOUNT');
insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit) values ('DCM0000002',null,5000,'INFINITE','이동전화 요금제 연계(all/life) 5000원 할인(프라임)','IMMEDIATELY','MOBILE_PHONE_PRICE_PLAN_LINKED','AMOUNT');

insert into dc_plcy (dc_plcy_cd, copn_plcy_cd, dc_amt_or_rate, dc_prd_typ, dc_plcy_nm, dc_sta_dt_typ, dc_typ, dc_unit)
values ('DCS0000001',null,4900,'THREE_MONTHS','이동전화 요금제 연계(all/life) 4900원 추가할인(프라임)_프로모션(20220901~20221231)','IMMEDIATELY','MOBILE_PHONE_PRICE_PLAN_LINKED','AMOUNT');
