function proj_visuals

%Task 1

    data = load('task1/results_gobackn_twomc');
    data(:, 1) = log2(data(:, 1));
    generate_single_plot(data, 'Transfer Time (sec) vs Window Size (log scale) (Go Back N)',...
                         'Window Size (log scale)', 'Transfer Time (sec)',...
                         {'Timeout = 20 msec'}, 'task1/figure_task1_gobackn.eps', 1);
                     
    data = load('task1/results_selrepeat_twomc');
    data(:, 1) = log2(data(:, 1));
    generate_single_plot(data, 'Transfer Time (sec) vs Window Size (log scale) (Sel. Repeat)',...
                         'Window Size (log scale)', 'Transfer Time (sec)',...
                         {'Timeout = 20 msec'}, 'task1/figure_task1_selrepeat.eps', 1);
                     
%Task 2
    data = load('task2/results_gobackn_twomc');
    generate_single_plot(data, 'Transfer Time (sec) vs Segment Size (bytes) (Go Back N)',...
                         'Segment Size (bytes)', 'Transfer Time (sec)',...
                         {'Timeout = 20 msec'}, 'task2/figure_task2_gobackn.eps', 1);
    
    data = load('task2/results_task2');
    generate_single_plot(data, 'Transfer Time (sec) vs Segment Size (bytes) (Sel. Repeat)',...
                         'Segment Size (bytes)', 'Transfer Time (sec)',...
                         {'Timeout = 20 msec'}, 'task2/figure_task2_selrepeat.eps', 1);                 
                     
%Task 3
    data = load('task3/results_gobackn_twomc');
    prob = 0.01:0.01:0.1;    
    data = [prob' data];
    generate_single_plot(data, 'Transfer Time (sec) vs Segment Loss Probability (Go Back N)' ,...
                         'Loss Probability', 'Transfer Time (sec)',...
                         {'Timeout = 20 msec'}, 'task3/figure_task3_gobackn.eps', 1);
                     
    data = load('task3/results_selrepeat_twomc');
    prob = 0.01:0.01:0.1;    
    data = [prob' data];
    generate_single_plot(data, 'Transfer Time (sec) vs Segment Loss Probability (Sel. Repeat)' ,...
                         'Loss Probability', 'Transfer Time (sec)',...
                         {'Timeout = 20 msec'}, 'task3/figure_task3_selrepeat.eps', 1);                     
                     
                     
end


function generate_single_plot(data, ttl, xlbl, ylbl, lgnd, outputfilename, plotConfInterval)

figure(1);
confInterval = (data(:, 4) - data(:, 3))/2;

plot(data(:, 1), data(:, 2), 'rs-', 'LineWidth', 2, 'MarkerSize', 1, 'MarkerFaceColor' , [.49 1 .63], 'MarkerEdgeColor', 'k');
hold on;
confInterval = (data(:, 4) - data(:, 3))/2;
if (plotConfInterval)
    errorbar(data(:, 1), data(:, 2), confInterval);      
end
grid on;

hTitle = title(ttl);
hXLabel = xlabel(xlbl);
hYLabel = ylabel(ylbl);
hLegend = legend(lgnd, 'Location', 'Best');

set(gca,'FontName','Helvetica')

set([hTitle, hXLabel, hYLabel, hLegend], 'FontName', 'Helvetica', 'FontWeight', 'bold');
set([hLegend, gca], 'FontSize', 10);
set([hXLabel, hYLabel], 'FontSize', 12)
set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
grid on
set(gca, ...
    'Box'         , 'off'     , ...
    'TickDir'     , 'out'     , ...
    'TickLength'  , [.02 .02] , ...
    'XMinorTick'  , 'on'      , ...
    'YMinorTick'  , 'on'      , ...
    'YGrid'       , 'on'      , ...
    'XColor'      , [.3 .3 .3], ...
    'YColor'      , [.3 .3 .3], ...
    'LineWidth'   , 1         );
axis fill
set(gcf, 'PaperPositionMode', 'auto');
print('-depsc2', outputfilename);
close;

end

% %task2 1K
% 
% figure(3);
% clr = load('task2_1k/clr.result');
% 
% plot(10:10:100, clr, 'b*-', 'LineWidth', 2);
% grid on;
% 
% hTitle = title('CLR vs K');
% hXLabel = xlabel('K');
% hYLabel = ylabel('CLR');
% hLegend = legend({'C = 1,000'}, 'Location', 'Best');
% 
% set(gca,'FontName','Helvetica')
% 
% set([hTitle, hXLabel, hYLabel, hLegend], 'FontName', 'Helvetica', 'FontWeight', 'bold');
% set([hLegend, gca], 'FontSize', 10);
% set([hXLabel, hYLabel], 'FontSize', 12)
% set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
% grid on
% set(gca, ...
% 'Box'         , 'off'     , ...
% 'TickDir'     , 'out'     , ...
% 'TickLength'  , [.02 .02] , ...
% 'XMinorTick'  , 'on'      , ...
% 'YMinorTick'  , 'on'      , ...
% 'YGrid'       , 'on'      , ...
% 'XColor'      , [.3 .3 .3], ...
% 'YColor'      , [.3 .3 .3], ...
% 'LineWidth'   , 1         );
% axis fill
% set(gcf, 'PaperPositionMode', 'auto');
% print('-depsc2', 'figure_task2_1k.eps');
% close;
% %task2 100K
% 
% figure(4);
% clr = load('task2_100k/clr.result');
% 
% plot(10:10:100, clr, 'rs-', 'LineWidth', 2);
% grid on;
% 
% hTitle = title('CLR vs K');
% hXLabel = xlabel('K');
% hYLabel = ylabel('CLR');
% hLegend = legend({'C = 100,000'}, 'Location', 'Best');
% 
% set(gca,'FontName','Helvetica')
% 
% set([hTitle, hXLabel, hYLabel, hLegend], 'FontName', 'Helvetica', 'FontWeight', 'bold');
% set([hLegend, gca], 'FontSize', 10);
% set([hXLabel, hYLabel], 'FontSize', 12)
% set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
% grid on
% set(gca, ...
% 'Box'         , 'off'     , ...
% 'TickDir'     , 'out'     , ...
% 'TickLength'  , [.02 .02] , ...
% 'XMinorTick'  , 'on'      , ...
% 'YMinorTick'  , 'on'      , ...
% 'YGrid'       , 'on'      , ...
% 'XColor'      , [.3 .3 .3], ...
% 'YColor'      , [.3 .3 .3], ...
% 'LineWidth'   , 1         );
% axis fill
% set(gcf, 'PaperPositionMode', 'auto');
% print('-depsc2', 'figure_task2_100k.eps');
% close;
% 
% %task3
% 
% figure(5);
% th_clr = zeros(10, 1);
% rho = 0.05:0.1:0.95;
% for i = 1:length(rho)
%     th_clr(i) = (1 - rho(i)) * (rho(i)^20) / (1 - rho(i)^21);
% end
% 
% clr = load('task3/clr.result');
% 
% plot(0.05:0.1:0.95, th_clr, 'b*-', 'LineWidth', 2); hold on;
% plot(0.05:0.1:0.95, clr, 'rs-', 'LineWidth', 2);
% grid on;
% 
% hTitle = title('CLR vs \rho, C = 100,000');
% hXLabel = xlabel('\rho');
% hYLabel = ylabel('CLR');
% hLegend = legend({'Analytical', 'Experimental'}, 'Location', 'Best');
% 
% set(gca,'FontName','Helvetica')
% 
% set([hTitle, hXLabel, hYLabel, hLegend], 'FontName', 'Helvetica', 'FontWeight', 'bold');
% set([hLegend, gca], 'FontSize', 10);
% set([hXLabel, hYLabel], 'FontSize', 12)
% set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
% grid on
% set(gca, ...
% 'Box'         , 'off'     , ...
% 'TickDir'     , 'out'     , ...
% 'TickLength'  , [.02 .02] , ...
% 'XMinorTick'  , 'on'      , ...
% 'YMinorTick'  , 'on'      , ...
% 'YGrid'       , 'on'      , ...
% 'XColor'      , [.3 .3 .3], ...
% 'YColor'      , [.3 .3 .3], ...
% 'LineWidth'   , 1         );
% axis fill
% set(gcf, 'PaperPositionMode', 'auto');
% print('-depsc2', 'figure_task3.eps');
% close;
% 
% %task4
% 
% figure(6);
% waiting = load('task4/waiting.result');
% 
% plot(0.05:0.1:0.95, waiting, 'rs-', 'LineWidth', 2); hold on;
% grid on;
% 
% hTitle = title('Average waiting time (sec) vs \rho, C = 100,000');
% hXLabel = xlabel('\rho');
% hYLabel = ylabel('Average waiting time (sec)');
% hLegend = legend({'C = 100,000'}, 'Location', 'Best');
% 
% set(gca,'FontName','Helvetica')
% 
% set([hTitle, hXLabel, hYLabel, hLegend], 'FontName', 'Helvetica', 'FontWeight', 'bold');
% set([hLegend, gca], 'FontSize', 10);
% set([hXLabel, hYLabel], 'FontSize', 12)
% set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
% grid on
% set(gca, ...
% 'Box'         , 'off'     , ...
% 'TickDir'     , 'out'     , ...
% 'TickLength'  , [.02 .02] , ...
% 'XMinorTick'  , 'on'      , ...
% 'YMinorTick'  , 'on'      , ...
% 'YGrid'       , 'on'      , ...
% 'XColor'      , [.3 .3 .3], ...
% 'YColor'      , [.3 .3 .3], ...
% 'LineWidth'   , 1         );
% axis fill
% set(gcf, 'PaperPositionMode', 'auto');
% print('-depsc2', 'figure_task4.eps');
% close;
% 
% %task5
% 
% figure(6);
% sim = load('task5/sim.result');
% 
% plot(0.05:0.1:0.95, sim, 'rs-', 'LineWidth', 2); hold on;
% grid on;
% 
% hTitle = title('Simulation time (sec) vs \rho, C = 100,000');
% hXLabel = xlabel('\rho');
% hYLabel = ylabel('Simulation time (sec)');
% hLegend = legend({'K = 40, C = 100,000'}, 'Location', 'Best');
% 
% set(gca,'FontName','Helvetica')
% 
% set([hTitle, hXLabel, hYLabel, hLegend], 'FontName', 'Helvetica', 'FontWeight', 'bold');
% set([hLegend, gca], 'FontSize', 10);
% set([hXLabel, hYLabel], 'FontSize', 12)
% set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
% grid on
% set(gca, ...
% 'Box'         , 'off'     , ...
% 'TickDir'     , 'out'     , ...
% 'TickLength'  , [.02 .02] , ...
% 'XMinorTick'  , 'on'      , ...
% 'YMinorTick'  , 'on'      , ...
% 'YGrid'       , 'on'      , ...
% 'XColor'      , [.3 .3 .3], ...
% 'YColor'      , [.3 .3 .3], ...
% 'LineWidth'   , 1         );
% axis fill
% set(gcf, 'PaperPositionMode', 'auto');
% print('-depsc2', 'figure_task5.eps');
% close;
