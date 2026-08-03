declare module "uview-plus" {
  export function install(): void;

  // 由于 uview-plus 库没有提供类型定义，如果想有需要自己定义
  // interface test {
  //   /** 邮箱格式校验 */
  //   email(email: string): boolean;
  // }
  // interface $u {
  //   test: test;
  // }

  global {
    interface Uni {
      $u: any;
      // $u: $u;
    }
  }
}
