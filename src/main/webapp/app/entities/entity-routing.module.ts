import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'app-user',
        data: { pageTitle: 'eRestaurantApp.appUser.home.title' },
        loadChildren: () => import('./app-user/app-user.module').then(m => m.AppUserModule),
      },
      {
        path: 'admin',
        data: { pageTitle: 'eRestaurantApp.admin.home.title' },
        loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
      },
      {
        path: 'restaurant',
        data: { pageTitle: 'eRestaurantApp.restaurant.home.title' },
        loadChildren: () => import('./restaurant/restaurant.module').then(m => m.RestaurantModule),
      },
      {
        path: 'customer',
        data: { pageTitle: 'eRestaurantApp.customer.home.title' },
        loadChildren: () => import('./customer/customer.module').then(m => m.CustomerModule),
      },
      {
        path: 'customer-profile',
        data: { pageTitle: 'eRestaurantApp.customerProfile.home.title' },
        loadChildren: () => import('./customer-profile/customer-profile.module').then(m => m.CustomerProfileModule),
      },
      {
        path: 'products',
        data: { pageTitle: 'eRestaurantApp.products.home.title' },
        loadChildren: () => import('./products/products.module').then(m => m.ProductsModule),
      },
      {
        path: 'discount',
        data: { pageTitle: 'eRestaurantApp.discount.home.title' },
        loadChildren: () => import('./discount/discount.module').then(m => m.DiscountModule),
      },
      {
        path: 'app-discount',
        data: { pageTitle: 'eRestaurantApp.appDiscount.home.title' },
        loadChildren: () => import('./app-discount/app-discount.module').then(m => m.AppDiscountModule),
      },
      {
        path: 'restaurant-discount',
        data: { pageTitle: 'eRestaurantApp.restaurantDiscount.home.title' },
        loadChildren: () => import('./restaurant-discount/restaurant-discount.module').then(m => m.RestaurantDiscountModule),
      },
      {
        path: 'categories',
        data: { pageTitle: 'eRestaurantApp.categories.home.title' },
        loadChildren: () => import('./categories/categories.module').then(m => m.CategoriesModule),
      },
      {
        path: 'cart',
        data: { pageTitle: 'eRestaurantApp.cart.home.title' },
        loadChildren: () => import('./cart/cart.module').then(m => m.CartModule),
      },
      {
        path: 'order',
        data: { pageTitle: 'eRestaurantApp.order.home.title' },
        loadChildren: () => import('./order/order.module').then(m => m.OrderModule),
      },
      {
        path: 'order-type',
        data: { pageTitle: 'eRestaurantApp.orderType.home.title' },
        loadChildren: () => import('./order-type/order-type.module').then(m => m.OrderTypeModule),
      },
      {
        path: 'payment',
        data: { pageTitle: 'eRestaurantApp.payment.home.title' },
        loadChildren: () => import('./payment/payment.module').then(m => m.PaymentModule),
      },
      {
        path: 'ad',
        data: { pageTitle: 'eRestaurantApp.ad.home.title' },
        loadChildren: () => import('./ad/ad.module').then(m => m.AdModule),
      },
      {
        path: 'restaurant-ad',
        data: { pageTitle: 'eRestaurantApp.restaurantAd.home.title' },
        loadChildren: () => import('./restaurant-ad/restaurant-ad.module').then(m => m.RestaurantAdModule),
      },
      {
        path: 'app-ad',
        data: { pageTitle: 'eRestaurantApp.appAd.home.title' },
        loadChildren: () => import('./app-ad/app-ad.module').then(m => m.AppAdModule),
      },
      {
        path: 'notification',
        data: { pageTitle: 'eRestaurantApp.notification.home.title' },
        loadChildren: () => import('./notification/notification.module').then(m => m.NotificationModule),
      },
      {
        path: 'notification-type',
        data: { pageTitle: 'eRestaurantApp.notificationType.home.title' },
        loadChildren: () => import('./notification-type/notification-type.module').then(m => m.NotificationTypeModule),
      },
      {
        path: 'employee',
        data: { pageTitle: 'eRestaurantApp.employee.home.title' },
        loadChildren: () => import('./employee/employee.module').then(m => m.EmployeeModule),
      },
      {
        path: 'profile',
        data: { pageTitle: 'eRestaurantApp.profile.home.title' },
        loadChildren: () => import('./profile/profile.module').then(m => m.ProfileModule),
      },
      {
        path: 'role',
        data: { pageTitle: 'eRestaurantApp.role.home.title' },
        loadChildren: () => import('./role/role.module').then(m => m.RoleModule),
      },
      {
        path: 'permission',
        data: { pageTitle: 'eRestaurantApp.permission.home.title' },
        loadChildren: () => import('./permission/permission.module').then(m => m.PermissionModule),
      },
      {
        path: 'simple-permission',
        data: { pageTitle: 'eRestaurantApp.simplePermission.home.title' },
        loadChildren: () => import('./simple-permission/simple-permission.module').then(m => m.SimplePermissionModule),
      },
      {
        path: 'permission-composite',
        data: { pageTitle: 'eRestaurantApp.permissionComposite.home.title' },
        loadChildren: () => import('./permission-composite/permission-composite.module').then(m => m.PermissionCompositeModule),
      },
      {
        path: 'state',
        data: { pageTitle: 'eRestaurantApp.state.home.title' },
        loadChildren: () => import('./state/state.module').then(m => m.StateModule),
      },
      {
        path: 'new-order',
        data: { pageTitle: 'eRestaurantApp.newOrder.home.title' },
        loadChildren: () => import('./new-order/new-order.module').then(m => m.NewOrderModule),
      },
      {
        path: 'cancel',
        data: { pageTitle: 'eRestaurantApp.cancel.home.title' },
        loadChildren: () => import('./cancel/cancel.module').then(m => m.CancelModule),
      },
      {
        path: 'complete',
        data: { pageTitle: 'eRestaurantApp.complete.home.title' },
        loadChildren: () => import('./complete/complete.module').then(m => m.CompleteModule),
      },
      {
        path: 'refunded',
        data: { pageTitle: 'eRestaurantApp.refunded.home.title' },
        loadChildren: () => import('./refunded/refunded.module').then(m => m.RefundedModule),
      },
      {
        path: 'on-hold',
        data: { pageTitle: 'eRestaurantApp.onHold.home.title' },
        loadChildren: () => import('./on-hold/on-hold.module').then(m => m.OnHoldModule),
      },
      {
        path: 'close',
        data: { pageTitle: 'eRestaurantApp.close.home.title' },
        loadChildren: () => import('./close/close.module').then(m => m.CloseModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
