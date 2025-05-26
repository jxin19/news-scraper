import { DashboardNavigation } from "@/components/dashboard/dashboard-navigation";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex flex-col min-h-screen">
      <DashboardNavigation />
      <main className="flex-1 p-6">{children}</main>
    </div>
  );
}
