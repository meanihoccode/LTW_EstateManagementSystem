SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

REAL ESTATE RENTAL MANAGEMENT SYSTEM

**I. GIỚI THIỆU**

**1. Mục đích**

> Tài liệu Software Requirements Specification (SRS) này mô tả chi tiết
> các yêu cầu chức năng và phi chức năng của Hệ thống Quản lý Bất động
> sản cho thuê.\
> Tài liệu nhằm cung cấp cái nhìn tổng thể và chi tiết về hệ thống cho
> các bên liên quan như giảng viên hướng dẫn, nhóm phát triển, kiểm thử
> và bảo trì hệ thống.
>
> Hệ thống được xây dựng nhằm hỗ trợ việc quản lý thông tin bất động
> sản, khách thuê, hợp đồng thuê, thanh toán và nhân sự một cách hiệu
> quả, chính xác và dễ sử dụng.

**2. Phạm vi dự án**

> Hệ thống được thiết kế để phục vụ hoạt động quản lý cho thuê bất động
> sản trong phạm vi nội bộ của doanh nghiệp hoặc cá nhân kinh doanh bất
> động sản.
>
> Phạm vi của dự án bao gồm:

- Quản lý thông tin bất động sản cho thuê

- Quản lý chủ sở hữu bất động sản

- Quản lý khách thuê

- Quản lý hợp đồng thuê

- Quản lý và theo dõi thanh toán

- Quản lý nhân viên hệ thống

- Phân quyền và kiểm soát truy cập người dùng

- Cung cấp giao diện web trực quan, dễ sử dụng

**3. Định nghĩa, thuật ngữ và từ viết tắt**

- BĐS: Bất động sản

- Manager: Người quản lý hệ thống

- Staff: Nhân viên vận hành

- Contract: Hợp đồng thuê

- Payment: Thanh toán

- SRS: Software Requirements Specification

4\. Tài liệu tham khảo

- IEEE Std 830 -- Software Requirements Specification

- Giáo trình Phân tích và Thiết kế Hệ thống Thông tin

- Tài liệu SQL Server Documentation

**II. TỔNG QUAN HỆ THỐNG**

**1. Tổng quan về sản phẩm**

> Real Estate Rental Management System là một hệ thống web nội bộ dùng
> để quản lý toàn bộ hoạt động cho thuê bất động sản.\
> Hệ thống cho phép người quản lý theo dõi tình trạng bất động sản, quản
> lý hợp đồng thuê, khách thuê, chủ sở hữu và các khoản thanh toán liên
> quan.
>
> Hệ thống hoạt động độc lập, không phụ thuộc trực tiếp vào các hệ thống
> bên ngoài.

**2. Kiến trúc hệ thống**

> Hệ thống được xây dựng theo mô hình Client -- Server:

- Client: Trình duyệt web của người dùng

- Server: Xử lý nghiệp vụ, xác thực, phân quyền

- Database: Lưu trữ dữ liệu liên quan đến BĐS, hợp đồng, khách thuê,
  thanh toán, nhân viên

**3. Lớp người dùng**

Hệ thống có hai lớp người dùng chính:

3.1. Manager (Quản lý)

- Quản lý toàn bộ dữ liệu hệ thống

- Tạo, chỉnh sửa, xóa bất động sản

- Quản lý hợp đồng và thanh toán

- Quản lý nhân viên

3.2. Staff (Nhân viên)

- Xem danh sách bất động sản được phân công

- Theo dõi hợp đồng liên quan

- Ghi nhận thanh toán

- Cập nhật trạng thái bất động sản

**4. Use case diagram**

![](media/image1.png){width="5.354913604549432in"
height="6.625925196850393in"}

**III. YÊU CẦU CHỨC NĂNG (FUNCTIONAL REQUIREMENTS)**

**1. Đăng nhập **

1.1. Mô tả 

Cho phép người dùng đăng nhập vào hệ thống.Hệ thống xác thực thông  tin
đăng nhập và phân quyền truy cập dựa trên vai trò của người dùng 

1.2. Use case specification và sequence diagram 

+-----------------+--------------------------------------------------------+
| Use case name   | Login                                                  |
+=================+========================================================+
| Actor           | User                                                   |
+-----------------+--------------------------------------------------------+
| Description     | Người dùng đăng nhập vào hệ thống bằng tài  khoản cá   |
|                 | nhân                                                   |
+-----------------+--------------------------------------------------------+
| Pre-condition   | Người dùng đã có tài khoản                             |
+-----------------+--------------------------------------------------------+
| Post-condition  | Người dùng truy cập vào hệ thống                       |
+-----------------+--------------------------------------------------------+
| Basic path      | 1.User nhập username và password                       |
|                 |                                                        |
|                 | 2.Hệ thống kiểm tra thông tin đăng nhập 3.Hệ thống xác |
|                 | thực thành công                                        |
|                 |                                                        |
|                 | 3.Hệ thống chuyển người dùng đến trang chính           |
+-----------------+--------------------------------------------------------+
| Alternative     | Thông tin đăng nhập không hợp lệ→ hệ thống  thông báo  |
| path            | lỗi                                                    |
+-----------------+--------------------------------------------------------+

\- Sequence diagram

![](media/image2.png){width="6.5in" height="3.701388888888889in"}

**2. Quản lý Thanh toán (Manage Payment)**

2.1. Mô tả

- Chức năng quản lý thanh toán cho phép Staff (Nhân viên) ghi nhận các
  khoản tiền thu từ khách thuê (tiền cọc, tiền thuê tháng) và cho phép
  Manager (Quản lý) phê duyệt, chỉnh sửa hoặc xóa các giao dịch bị sai
  sót.

- Chức năng này đóng vai trò quan trọng trong việc kiểm soát dòng tiền,
  theo dõi công nợ của từng hợp đồng, đảm bảo không xảy ra thất thoát
  tài chính và nhắc nhở thu hồi công nợ đúng hạn.

2.2. Use case specification và Sequence diagram

+-----------------------------------+-----------------------------------+
| Use case name                     | Manage Payment (Quản lý Thanh     |
|                                   | toán)                             |
+===================================+===================================+
| Actor                             | Staff, Manager                    |
+-----------------------------------+-----------------------------------+
| Description                       | Cho phép ghi nhận, phê duyệt, xem |
|                                   | lịch sử và xóa các khoản thanh    |
|                                   | toán liên quan đến hợp đồng cho   |
|                                   | thuê.                             |
+-----------------------------------+-----------------------------------+
| Pre-condition                     | Người dùng đã đăng nhập vào hệ    |
|                                   | thống.                            |
|                                   |                                   |
|                                   | Hợp đồng tương ứng đang ở trạng   |
|                                   | thái có hiệu lực và có phát sinh  |
|                                   | công nợ (chưa thanh toán hết).    |
+-----------------------------------+-----------------------------------+
| Post-condition                    | Thông tin thanh toán được lưu trữ |
|                                   | vào CSDL.                         |
|                                   |                                   |
|                                   | Trạng thái công nợ của Hợp đồng   |
|                                   | tự động được cập nhật.            |
+-----------------------------------+-----------------------------------+
| Basic path                        | 1\. Nhân viên/Quản lý đăng nhập   |
|                                   | vào hệ thống.                     |
|                                   |                                   |
|                                   | 2\. Chọn menu \"Quản lý Thanh     |
|                                   | toán\" hoặc chọn \"Thanh toán\"   |
|                                   | trực tiếp từ một Hợp đồng.        |
|                                   |                                   |
|                                   | 3\. Nhập thông tin thanh toán (Số |
|                                   | tiền, Ngày thu, Phương thức: Tiền |
|                                   | mặt/Chuyển khoản).                |
|                                   |                                   |
|                                   | 4\. Nhấn nút \"Xác nhận\".        |
|                                   |                                   |
|                                   | 5\. Hệ thống xác thực dữ liệu (số |
|                                   | tiền \> 0, ngày thanh toán hợp    |
|                                   | lệ).                              |
|                                   |                                   |
|                                   | 6\. Hệ thống lưu giao dịch vào cơ |
|                                   | sở dữ liệu và trừ vào công nợ của |
|                                   | Hợp đồng.                         |
|                                   |                                   |
|                                   | 7\. Hệ thống thông báo giao dịch  |
|                                   | thành công.                       |
+-----------------------------------+-----------------------------------+
| Alternative path                  | A1. Dữ liệu không hợp lệ: Hệ      |
|                                   | thống báo lỗi \"Số tiền phải lớn  |
|                                   | hơn 0\" hoặc \"Chưa chọn phương   |
|                                   | thức thanh toán\".                |
|                                   |                                   |
|                                   | A2. Số tiền nhập lớn hơn số nợ:   |
|                                   | Hệ thống hiện cảnh báo \"Số tiền  |
|                                   | thanh toán vượt quá công nợ hiện  |
|                                   | tại\", yêu cầu người dùng xác     |
|                                   | nhận lại.                         |
+-----------------------------------+-----------------------------------+

-Sequence Diagram

![](media/image3.png){width="6.5in" height="3.0944444444444446in"}

2.3. Yêu cầu chức năng (Functional Requirements)

  -----------------------------------------------------------------------
  ID       Functions             Requirement (Yêu cầu)
  -------- --------------------- ----------------------------------------
  FR-1     Record Payment        Hệ thống cho phép Staff/Manager ghi nhận
                                 các khoản thu (tiền cọc, tiền thuê).

  FR-2     Update Payment        Hệ thống cho phép Manager chỉnh sửa
                                 thông tin giao dịch nếu có sai sót nhập
                                 liệu.

  FR-3     Delete Payment        Hệ thống cho phép Manager xóa (hủy) giao
                                 dịch bị lỗi.

  FR-4     View Payment History  Hệ thống cho phép Staff/Manager tra cứu
                                 lịch sử thanh toán theo Hợp đồng, theo
                                 Thời gian.

  FR-5     Update Contract Debt  Hệ thống tự động trừ công nợ của hợp
                                 đồng tương ứng sau khi ghi nhận thanh
                                 toán thành công.
  -----------------------------------------------------------------------

**3. Quản lý Hợp đồng (Manage Contract)**

3.1. Mô tả

- Chức năng quản lý hợp đồng cho phép Manager tạo mới, phân công theo
  dõi, cập nhật, thanh lý hoặc xóa hợp đồng cho thuê, đồng thời cho phép
  các Staff xem các hợp đồng do mình phụ trách và cập nhật trạng thái
  (đã thu tiền, đến hạn\...).

- Chức năng giúp kiểm soát chặt chẽ tính pháp lý của BĐS, đảm bảo dòng
  tiền được thu đúng hạn và quản lý tốt thông tin của khách thuê.

3.2. Use case specification và Sequence diagram

+-----------------------------------+-----------------------------------+
| Use case name                     | Manage Contract (Quản lý Hợp      |
|                                   | đồng)                             |
+===================================+===================================+
| Actor                             | Manager                           |
+-----------------------------------+-----------------------------------+
| Description                       | Cho phép người quản lý có thể tạo |
|                                   | mới, cập nhật, phân công, xem,    |
|                                   | xoá các hợp đồng cho thuê bất     |
|                                   | động sản.                         |
+-----------------------------------+-----------------------------------+
| Pre-condition                     | Người quản lý đã được xác thực    |
|                                   | (đăng nhập thành công).           |
|                                   |                                   |
|                                   | Bất động sản và Khách thuê tương  |
|                                   | ứng đã tồn tại trong hệ thống.    |
+-----------------------------------+-----------------------------------+
| Post-condition                    | Thông tin hợp đồng được lưu trữ   |
|                                   | hoặc cập nhật vào CSDL.           |
|                                   |                                   |
|                                   | Nhân viên (Staff) được phân công  |
|                                   | phụ trách hợp đồng nhận được      |
|                                   | thông báo.                        |
+-----------------------------------+-----------------------------------+
| Basic path                        | 1\. Quản lý đăng nhập vào hệ      |
|                                   | thống.                            |
|                                   |                                   |
|                                   | 2\. Quản lý vào menu \"Quản lý    |
|                                   | Hợp đồng\".                       |
|                                   |                                   |
|                                   | 3\. Quản lý chọn tạo mới hoặc cập |
|                                   | nhật một hợp đồng hiện có.        |
|                                   |                                   |
|                                   | 4\. Quản lý nhập thông tin chi    |
|                                   | tiết (Thời hạn, Giá, Tiền         |
|                                   | cọc\...) và phân công cho Nhân    |
|                                   | viên phụ trách.                   |
|                                   |                                   |
|                                   | 5\. Quản lý nhấn \"Lưu\".         |
|                                   |                                   |
|                                   | 6\. Hệ thống xác thực tính hợp lệ |
|                                   | của dữ liệu (ngày kết thúc \>     |
|                                   | ngày bắt đầu, BĐS đang            |
|                                   | trống\...).                       |
|                                   |                                   |
|                                   | 7\. Hệ thống lưu hợp đồng vào cơ  |
|                                   | sở dữ liệu.                       |
|                                   |                                   |
|                                   | 8\. Hệ thống gửi thông báo nhiệm  |
|                                   | vụ theo dõi hợp đồng đến nhân     |
|                                   | viên được phân công.              |
+-----------------------------------+-----------------------------------+
| Alternative path                  | A1. Thông tin hợp đồng không hợp  |
|                                   | lệ: Hệ thống báo lỗi \"Ngày tháng |
|                                   | không hợp lệ\" hoặc \"BĐS đã có   |
|                                   | người thuê\".                     |
|                                   |                                   |
|                                   | A2. Khách thuê không tồn tại: Hệ  |
|                                   | thống yêu cầu tạo mới khách thuê  |
|                                   | trước.                            |
+-----------------------------------+-----------------------------------+

-Sequence Diagram:

![](media/image4.png){width="6.5in" height="3.1041666666666665in"}

3.3. Yêu cầu chức năng (Functional Requirements)

  -----------------------------------------------------------------------
  ID       Functions             Requirement (Yêu cầu)
  -------- --------------------- ----------------------------------------
  FR-1     Create Contract       Hệ thống cho phép Manager tạo hợp đồng
                                 mới (liên kết BĐS và Khách thuê).

  FR-2     Edit Contract         Hệ thống cho phép Manager chỉnh sửa điều
                                 khoản hợp đồng (thời hạn, giá thuê).

  FR-3     Delete Contract       Hệ thống cho phép Manager thanh lý hoặc
                                 xóa hợp đồng sai sót.

  FR-4     Assign Contract       Hệ thống cho phép Manager gán hợp đồng
                                 cho một Staff phụ trách theo dõi.

  FR-5     View Contract         Hệ thống cho phép Staff xem chi tiết các
                                 hợp đồng mình được phân công.

  FR-6     Update Status         Hệ thống cho phép Staff cập nhật trạng
                                 thái hợp đồng (Đang hiệu lực, Chờ thanh
                                 toán, Hết hạn).
  -----------------------------------------------------------------------

**4. Quản lý Bất động sản (Manage Real Estate)**

4.1. Mô tả

- Chức năng quản lý bất động sản cho phép Manager thêm mới, chỉnh sửa
  thông tin (giá, diện tích, địa chỉ), xóa hoặc phân công quản lý các
  tài sản nhà đất. Đồng thời, cho phép các Staff xem danh sách BĐS được
  giao và cập nhật trạng thái thực tế của BĐS (Còn trống, Đã cho thuê,
  Đang sửa chữa).

- Chức năng này giúp doanh nghiệp nắm bắt chính xác số lượng tài sản,
  tối ưu hóa tỷ lệ lấp đầy (cho thuê) và bảo trì tài sản kịp thời.

4.2. Use case specification và Sequence diagram

+---------------------+------------------------------------------------+
| Use case name       | Manage Real Estate (Quản lý Bất động sản)      |
+=====================+================================================+
| Actor               | Manager                                        |
+---------------------+------------------------------------------------+
| Description         | Cho phép người quản lý tạo mới, phân công, cập |
|                     | nhật, xem, xoá các thông tin bất động sản      |
|                     | trong hệ thống.                                |
+---------------------+------------------------------------------------+
| Pre-condition       | Người quản lý đã đăng nhập thành công vào hệ   |
|                     | thống.                                         |
|                     |                                                |
|                     | Thông tin Chủ sở hữu (Owner) đã tồn tại (nếu   |
|                     | là BĐS ký gửi).                                |
+---------------------+------------------------------------------------+
| Post-condition      | Thông tin Bất động sản được lưu trữ hoặc cập   |
|                     | nhật vào CSDL.                                 |
|                     |                                                |
|                     | Nhân viên (Staff) được phân công quản lý BĐS   |
|                     | nhận được thông báo.                           |
+---------------------+------------------------------------------------+
| Basic path          | 1\. Quản lý đăng nhập vào hệ thống.            |
|                     |                                                |
|                     | 2\. Quản lý chọn menu \"Quản lý BĐS\".         |
|                     |                                                |
|                     | 3\. Quản lý chọn \"Thêm mới\" hoặc \"Cập       |
|                     | nhật\" BĐS.                                    |
|                     |                                                |
|                     | 4\. Quản lý nhập thông tin BĐS (Loại, Diện     |
|                     | tích, Giá\...) và phân công cho Nhân viên phụ  |
|                     | trách.                                         |
|                     |                                                |
|                     | 5\. Quản lý nhấn \"Lưu\".                      |
|                     |                                                |
|                     | 6\. Hệ thống xác thực dữ liệu (không trùng địa |
|                     | chỉ, giá trị hợp lệ).                          |
|                     |                                                |
|                     | 7\. Hệ thống lưu thông tin BĐS vào cơ sở dữ    |
|                     | liệu.                                          |
|                     |                                                |
|                     | 8\. Hệ thống gửi thông báo đến nhân viên được  |
|                     | phân công.                                     |
+---------------------+------------------------------------------------+
| Alternative path    | A1. Thông tin BĐS không hợp lệ: Hệ thống báo   |
|                     | lỗi (VD: Thiếu thông tin bắt buộc, Giá thuê \< |
|                     | 0).                                            |
|                     |                                                |
|                     | A2. BĐS đã tồn tại (trùng địa chỉ): Hệ thống   |
|                     | cảnh báo trùng lặp dữ liệu.                    |
+---------------------+------------------------------------------------+

-Sequence diagram:

![](media/image5.png){width="6.5in" height="3.040277777777778in"}

4.3. Yêu cầu chức năng (Functional Requirements)

  -----------------------------------------------------------------------
  ID       Functions             Requirement (Yêu cầu)
  -------- --------------------- ----------------------------------------
  FR-1     Create Real Estate    Hệ thống cho phép Manager thêm mới một
                                 bất động sản vào kho dữ liệu.

  FR-2     Edit Real Estate      Hệ thống cho phép Manager chỉnh sửa
                                 thông tin chi tiết của BĐS.

  FR-3     Delete Real Estate    Hệ thống cho phép Manager xóa hoặc ẩn
                                 (deactivate) BĐS không còn quản lý.

  FR-4     Assign Real Estate    Hệ thống cho phép Manager gán BĐS cho
                                 một Staff phụ trách quản lý thực địa.

  FR-5     View Real Estate      Hệ thống cho phép Staff xem danh sách và
                                 chi tiết các BĐS được giao.

  FR-6     Update BĐS Status     Hệ thống cho phép Staff cập nhật trạng
                                 thái BĐS (Còn trống, Đang cho thuê, Đang
                                 sửa chữa).
  -----------------------------------------------------------------------

**5. Quản lý Tài khoản (Account)**

Mô tả: Chức năng cơ bản để vào hệ thống.

- FR-AUTH-1: Đăng nhập (Phân quyền Manager/Staff).

- FR-AUTH-2: Đăng xuất. *(Bỏ: Đổi mật khẩu, quên mật khẩu, tự động khóa
  phiên).*

**6. Quản lý Đối tác (Partner)**

Mô tả: Quản lý thông tin khách thuê để làm hợp đồng.

- FR-PART-1: Thêm mới và Sửa thông tin Khách thuê (Họ tên, CCCD, SĐT).

- FR-PART-2: Xem danh sách Khách thuê.

**7. Quản lý Nhân sự**

Mô tả: Quản lý danh sách nhân viên để phân công công việc.

- FR-STAFF-1: Manager có thể thêm tài khoản Staff mới và xem danh sách
  Staff.

**IV. Entity Relationship Diagram (ERD)**

Sơ đồ quan hệ thực thể ERD:

![](media/image6.png){width="6.5in" height="3.1567968066491687in"}

Lược đồ quan hệ ER:

![](media/image7.png){width="6.5in" height="1.41669072615923in"}

![](media/image8.png){width="6.5in" height="4.363194444444445in"}

**IV. YÊU CẦU GIAO DIỆN (INTERFACE REQUIREMENTS)**

1.  Trang chủ

![](media/image9.png){width="6.5in" height="4.084722222222222in"}

2.  Trang đăng nhập:

![](media/image10.png){width="6.5in" height="4.102777777777778in"}

3.  Trang tổng quan:

![](media/image11.png){width="6.5in" height="3.5618055555555554in"}

4.  Trang quản lí BĐS

![](media/image12.png){width="6.5in" height="3.551388888888889in"}

5.  Trang quản lí hợp đồng

![](media/image13.png){width="6.5in" height="3.5541666666666667in"}

6.  Trang quản lí thanh toán

![](media/image14.png){width="6.5in" height="3.529166666666667in"}

7.  Trang quản lí nhân viên

![](media/image15.png){width="6.5in" height="3.2194444444444446in"}

**V. YÊU CẦU PHI CHỨC NĂNG (NON-FUNCTIONAL REQUIREMENTS)**

5.1. Hiệu năng

- Thời gian phản hồi \< 3 giây

- Hỗ trợ nhiều người dùng đồng thời

5.2. Bảo mật

- Phân quyền người dùng

- Mã hóa mật khẩu

- Ngăn truy cập trái phép

5.3. Khả năng mở rộng

- Có thể mở rộng thêm chức năng

- Dễ nâng cấp hệ thống

5.4. Khả năng sử dụng

- Giao diện đơn giản

- Dễ học, dễ sử dụng

5.5. Khả năng bảo trì

- Code rõ ràng

- Dễ bảo trì và sửa lỗi
