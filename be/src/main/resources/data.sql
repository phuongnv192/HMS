
INSERT INTO hms.tb_role (name) VALUES ('ADMIN');
INSERT INTO hms.tb_role (name) VALUES ('MANAGER');
INSERT INTO hms.tb_role (name) VALUES ('LEADER');
INSERT INTO hms.tb_role (name) VALUES ('CLEANER');
INSERT INTO hms.tb_role (name) VALUES ('CUSTOMER');

INSERT INTO hms.tb_user (create_date, role_id, update_by, update_date, email, first_name, gender, last_name, password, phone_number, status, username) VALUES ('2023-11-30 21:12:27.707000', 2, null, '2023-11-30 21:12:27.707000', null, 'hms manager', null, '', '$2a$10$3W8g40h7te7lnSLRq8Gtr.Q62GBLzFnqdJrz.tO3d/Xf3/ljMfbhS', null, 'ACTIVE', 'manager');
INSERT INTO hms.tb_user (create_date, role_id, update_by, update_date, email, first_name, gender, last_name, password, phone_number, status, username) VALUES ('2023-11-30 21:12:27.715000', 1, null, '2023-11-30 21:12:27.715000', null, 'hms admin', null, '', '$2a$10$R7BApYLGn/9WDr0DGcTml.0tYadcrQ95kMY0fOwDoAXNAO3/FeiRm', null, 'ACTIVE', 'admin');
INSERT INTO hms.tb_user (create_date, role_id, update_by, update_date, email, first_name, gender, last_name, password, phone_number, status, username) VALUES ('2023-11-30 21:12:27.716000', 4, null, '2023-11-30 21:12:27.716000', null, 'hms cleaner', null, '', '$2a$10$PVKH2IBic74NORdRl9Y1ZuGNNANxkTSzfz31hEXYxow7Q7Vw3lT06', null, 'ACTIVE', 'cleaner');
INSERT INTO hms.tb_user (create_date, role_id, update_by, update_date, email, first_name, gender, last_name, password, phone_number, status, username) VALUES ('2023-11-30 21:12:27.717000', 3, null, '2023-11-30 21:12:27.717000', null, 'hms leader', null, '', '$2a$10$hYfLERGa5h6mKZanh.uzyOUmUa7jCvN.2ZSQCiHEFxi6obaRCdTf2', null, 'ACTIVE', 'leader');

INSERT INTO hms.tb_branch (create_date, manager_id, update_date, branch_address, branch_name, description, status) VALUES ('2023-11-30 21:12:27.374000', null, '2023-11-30 21:12:27.374000', null, 'Ha Noi', null, 'ACTIVE');

INSERT INTO hms.tb_cleaner (branch_id, create_date, user_id, review, address, id_card, status) VALUES (1, '2023-11-30 21:12:27.719000', 1, null, null, '1', 'ACTIVE');
INSERT INTO hms.tb_cleaner (branch_id, create_date, user_id, review, address, id_card, status) VALUES (1, '2023-11-30 21:12:27.721000', 2, null, null, '2', 'ACTIVE');
INSERT INTO hms.tb_cleaner (branch_id, create_date, user_id, review, address, id_card, status) VALUES (1, '2023-11-30 21:12:27.724000', 4, null, null, '2', 'ACTIVE');
INSERT INTO hms.tb_cleaner (branch_id, create_date, user_id, review, address, id_card, status) VALUES (1, '2023-11-30 21:12:27.722000', 3, '{"1":{"cleanerActivities":[{"bookingScheduleId":1,"workDate":1701277200000,"ratingScore":5,"review":"very good"}]}}', null, '2', 'ACTIVE');

INSERT INTO hms.tb_service_type (service_type_name) VALUES ('Daily on the basis');
INSERT INTO hms.tb_service_type (service_type_name) VALUES ('Weekly on the basis');
INSERT INTO hms.tb_service_type (service_type_name) VALUES ('Monthly on the basis');

INSERT INTO hms.tb_service (service_type_id, description, payment_method, service_name, status) VALUES (1, 'Apartment cleaning', 'CASH', 'Apartment cleaning', 'ACTIVE');

INSERT INTO hms.tb_service_addon (parent_id, price, service_id, name, status) VALUES (null, 0, 1, 'Washing machine service', 'ACTIVE');
INSERT INTO hms.tb_service_addon (parent_id, price, service_id, name, status) VALUES (1, 10000, null, 'Vệ sinh máy giặt không tháo lồng', 'ACTIVE');
INSERT INTO hms.tb_service_addon (parent_id, price, service_id, name, status) VALUES (1, 20000, null, 'Vệ sinh và bảo trì máy giặt lồng', 'ACTIVE');

INSERT INTO hms.tb_service_package (service_type_id, service_package_name, unit) VALUES (1, '1', 'months');
INSERT INTO hms.tb_service_package (service_type_id, service_package_name, unit) VALUES (2, '3', 'months');
INSERT INTO hms.tb_service_package (service_type_id, service_package_name, unit) VALUES (3, '6', 'months');
INSERT INTO hms.tb_service_package (service_type_id, service_package_name, unit) VALUES (3, '12', 'months');

INSERT INTO hms.tb_booking (floor_area, floor_number, create_date, customer_id, update_date, updated_by_id, note, review, raw_request, host_address, host_distance, host_name, host_phone, house_type, rejected_reason, status) VALUES (60, 2, '2023-11-30 21:26:23.262000', 1, '2023-11-30 21:26:29.473000', 1, 'không có gì', null, '{"bookingId":null,"hostName":"hms system","hostPhone":"0369156413","hostAddress":"số 6, Minh Khai, Hà Nội","hostDistance":"6km","houseType":"APARTMENT","floorNumber":2,"floorArea":"M260","customerId":1,"cleanerIds":null,"bookingSchedules":[{"floorNumber":4,"workDate":[2023,11,30],"startTime":null,"endTime":null,"serviceAddOnIds":[]}],"serviceTypeId":3,"servicePackageId":3,"serviceAddOnIds":[3],"startTime":1701339147374,"endTime":1701360747374,"workDate":[2023,11,30],"note":"không có gì","paymentType":null}', 'số 6, Minh Khai, Hà Nội', '6km', 'hms system', '0369156413', 'APARTMENT', null, 'CONFIRMED');
INSERT INTO hms.tb_booking (floor_area, floor_number, create_date, customer_id, update_date, updated_by_id, note, review, raw_request, host_address, host_distance, host_name, host_phone, house_type, rejected_reason, status) VALUES (60, 2, '2023-11-30 21:18:21.340000', 1, '2023-11-30 21:18:27.044000', 1, 'làm cẩn thận', null, '{"bookingId":null,"hostName":"duongpl","hostPhone":"0369156413","hostAddress":"số 6, Minh Khai, Hà Nội","hostDistance":"6km","houseType":"APARTMENT","floorNumber":2,"floorArea":"M260","customerId":1,"cleanerIds":null,"bookingSchedules":[{"floorNumber":4,"workDate":[2023,11,30],"startTime":null,"endTime":null,"serviceAddOnIds":[]}],"serviceTypeId":3,"servicePackageId":3,"serviceAddOnIds":[3],"startTime":1701339147374,"endTime":1701360747374,"workDate":[2023,11,30],"note":"làm cẩn thận","paymentType":null}', 'số 6, Minh Khai, Hà Nội', '6km', 'normal customer', '0369156413', 'APARTMENT', null, 'CONFIRMED');

INSERT INTO hms.tb_booking_cleaners (booking_id, cleaners_id) VALUES (1, 4);
INSERT INTO hms.tb_booking_cleaners (booking_id, cleaners_id) VALUES (1, 3);
INSERT INTO hms.tb_booking_cleaners (booking_id, cleaners_id) VALUES (2, 1);
INSERT INTO hms.tb_booking_cleaners (booking_id, cleaners_id) VALUES (2, 2);

INSERT INTO hms.tb_booking_transaction (total_booking_cleaner, total_booking_date, total_booking_price, booking_id, create_date, service_package_id, update_date, status) VALUES (2, 7, 7020000, 1, '2023-11-30 21:18:26.967000', 3, '2023-11-30 21:18:27.064000', 'CONFIRMED');
INSERT INTO hms.tb_booking_transaction (total_booking_cleaner, total_booking_date, total_booking_price, booking_id, create_date, service_package_id, update_date, status) VALUES (2, 7, 7020000, 2, '2023-11-30 21:26:29.434000', 3, '2023-11-30 21:26:29.481000', 'CONFIRMED');

INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2023-12-30', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 1, 1, '2023-11-30 21:18:27.057000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-01-30', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 1, 1, '2023-11-30 21:18:27.059000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-02-29', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 1, 1, '2023-11-30 21:18:27.060000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-03-29', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 1, 1, '2023-11-30 21:18:27.061000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-04-29', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 1, 1, '2023-11-30 21:18:27.062000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-05-29', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 1, 1, '2023-11-30 21:18:27.063000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (2000000, '2023-11-30', null, null, 1, 1, '2023-11-30 21:23:01.573000', null, null, '', 'WITHDRAW', '5', 'DONE');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (2000000, '2023-11-30', null, null, 2, 1, '2023-11-30 21:26:29.474000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2023-12-30', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 2, 1, '2023-11-30 21:26:29.476000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-01-30', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 2, 1, '2023-11-30 21:26:29.477000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-02-29', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 2, 1, '2023-11-30 21:26:29.478000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-03-29', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 2, 1, '2023-11-30 21:26:29.479000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-04-29', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 2, 1, '2023-11-30 21:26:29.480000', null, null, null, null, null, 'RECEIVED');
INSERT INTO hms.tb_booking_schedule (total_schedule_price, work_date, end_time, start_time, transaction_id, update_by, update_date, cashback_status, day_of_the_week, payment_note, payment_status, rating_score, status) VALUES (1020000, '2024-05-29', '2023-11-30 23:12:27.374000', '2023-11-30 17:12:27.374000', 2, 1, '2023-11-30 21:26:29.480000', null, null, null, null, null, 'RECEIVED');

INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (2, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (3, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (4, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (5, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (6, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (7, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (9, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (10, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (11, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (12, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (13, 3);
INSERT INTO hms.tb_booking_schedule_service_add_ons (booking_schedule_schedule_id, service_add_ons_id) VALUES (14, 3);

INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2023-12-30', 3, 2, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-01-30', 3, 3, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-02-29', 3, 4, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-03-29', 3, 5, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-04-29', 3, 6, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-05-29', 3, 7, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2023-11-30', 4, 8, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2023-12-30', 4, 9, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-01-30', 4, 10, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-02-29', 4, 11, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-03-29', 4, 12, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-04-29', 4, 13, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-05-29', 4, 14, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2023-11-30', 3, 1, 'INACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2023-11-30', 1, 15, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2023-12-30', 1, 16, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-01-30', 1, 17, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-02-29', 1, 18, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-03-29', 1, 19, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-04-29', 1, 20, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-05-29', 1, 21, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2023-11-30', 2, 22, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2023-12-30', 2, 23, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-01-30', 2, 24, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-02-29', 2, 25, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-03-29', 2, 26, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-04-29', 2, 27, 'ACTIVE');
INSERT INTO hms.tb_cleaner_working_date (schedule_date, cleaner_id, id, status) VALUES ('2024-05-29', 2, 28, 'ACTIVE');
