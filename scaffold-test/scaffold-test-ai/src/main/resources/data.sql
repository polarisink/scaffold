MERGE INTO ai_demo_order (id, order_no, user_id, product_id, product_name, paid_amount, order_status, after_sale_status, receiver_phone, deleted)
KEY (order_no) VALUES
('demo-order-202607190001', '202607190001', 1, 'PHONE-X1', 'Scaffold Phone X1', 3999.00, 'DELIVERED', 'NONE', '13800000001', 0),
('demo-order-202607190002', '202607190002', 2, 'TABLET-PRO', 'Scaffold Tablet Pro', 2699.00, 'SHIPPED', 'NONE', '13800000002', 0),
('demo-order-202607190003', '202607190003', 1, 'WATCH-S', 'Scaffold Watch S', 899.00, 'PAID', 'NONE', '13800000003', 0);

MERGE INTO ai_demo_logistics (id, order_no, carrier, tracking_no, status, latest_description, latest_update_time, deleted)
KEY (order_no) VALUES
('demo-logistics-202607190001', '202607190001', '顺丰速运', 'SF-DEMO-0001', 'DELIVERED', '快件已由本人签收', TIMESTAMP '2026-07-19 16:30:00', 0),
('demo-logistics-202607190002', '202607190002', '京东物流', 'JD-DEMO-0002', 'IN_TRANSIT', '快件正在运往上海分拨中心', TIMESTAMP '2026-07-20 09:15:00', 0);
